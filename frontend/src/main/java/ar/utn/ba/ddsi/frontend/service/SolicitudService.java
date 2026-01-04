package ar.utn.ba.ddsi.frontend.service;

import ar.utn.ba.ddsi.frontend.models.dtos.*;
import ar.utn.ba.ddsi.frontend.models.dtos.EstadoSolicitud;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SolicitudService {

    final private HechoService hechoService;
    final private WebClient webClient;
    final private WebClient webClient2;
    final private ObjectMapper objectMapper;

    public SolicitudService(WebClient.Builder builder, HechoService hechoService,
                           @Value("${app.api.agregador.base-url:http://localhost:8084/api}") String agregadorBaseUrl,
                            @Value("${app.api.dinamica.base-url:http://localhost:8081/api}") String dinamicaBaseUrl,
                            ObjectMapper objectMapper) {
        this.webClient = builder.baseUrl(agregadorBaseUrl).build();
        this.webClient2 = builder.baseUrl(dinamicaBaseUrl).build();
        this.hechoService = hechoService;
        this.objectMapper = objectMapper;
    }

    public Long obtenerContribuyenteIdDeSesion() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            Object userIdObj = request.getSession().getAttribute("userId");
            if (userIdObj != null) {
                return userIdObj instanceof Long ? (Long) userIdObj : Long.valueOf(userIdObj.toString());
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private String getAccessTokenFromSession() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            return (String) request.getSession().getAttribute("accessToken");
        } catch (Exception e) {
            return null;
        }
    }

    public List<SolicitudInputDTO> buscarTodasLasSolicitudes() {
        try {
            String accessToken = getAccessTokenFromSession();
            if (accessToken == null) {
                log.error("No hay token de acceso disponible para buscar solicitudes");
                throw new RuntimeException("No hay token de acceso disponible. Por favor, inicie sesión nuevamente.");
            }
            log.info("Buscando todas las solicitudes con token disponible");
            List<SolicitudInputDTO> solicitudes = webClient2.get()
                    .uri("/admin/solicitudes")
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response -> {
                        log.error("Error HTTP al buscar solicitudes: {} - {}", response.statusCode(), response.statusCode().value());
                        return response.bodyToMono(String.class)
                                .doOnNext(body -> log.error("Cuerpo de respuesta de error: {}", body))
                                .flatMap(body -> Mono.<RuntimeException>error(new RuntimeException("Error del backend: " + response.statusCode() + " - " + body)));
                    })
                    .bodyToMono(new ParameterizedTypeReference<List<SolicitudInputDTO>>() {})
                    .block();
            
            if (solicitudes == null) {
                log.warn("El backend devolvió null para las solicitudes");
                return Collections.emptyList();
            }
            
            log.info("Se encontraron {} solicitudes", solicitudes.size());
            return solicitudes;

        } catch (WebClientResponseException e) {
            log.error("Error del WebClient al buscar todas las solicitudes: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Error al comunicarse con el backend: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error inesperado al buscar todas las solicitudes: {}", e.getMessage(), e);
            throw new RuntimeException("Error al buscar solicitudes: " + e.getMessage(), e);
        }
    }
  


    public SolicitudDTO crearSolicitud(SolicitudDTO solicitud) {
        return webClient.post()
                .uri("/solicitudes")
                .bodyValue(solicitud)
                .retrieve()
                .bodyToMono(SolicitudDTO.class)
                .block();
    }


    public List<SolicitudInputDTO> obtenerSolicitudesPorContribuyente(Long idContribuyente) {
        try {
            return webClient2.get()
                    .uri("/solicitudes/contribuyente/" + idContribuyente)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<SolicitudInputDTO>>() {})
                    .block();
        } catch (Exception e) {
            log.error("Error al obtener solicitudes por contribuyente {}: {}", idContribuyente, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public void ocultarHechoEnAgregador(Long idHecho) {
        try {
            webClient.patch()
                    .uri("/hechos/" + idHecho + "/ocultar")
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> response.bodyToMono(String.class).map(RuntimeException::new)
                    )
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Error del Backend: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException("No se pudo conectar con el servicio de Agregador", e);
        }
    }

    @Transactional
    public void procesarSolicitudEdicionConMultipart(Long idSolicitud, SolicitudEdicionFormDTO formDTO) {
        SolicitudInputDTO solicitud = webClient2.get()
                .uri("/solicitudes/" + idSolicitud)
                .retrieve()
                .bodyToMono(SolicitudInputDTO.class)
                .block();

        if (solicitud == null) {
            throw new IllegalArgumentException("No se encontró la solicitud con ID: " + idSolicitud);
        }

        if (solicitud.getFechaDeCargaSolicitud() != null) {
            long diasPasados = ChronoUnit.DAYS.between(solicitud.getFechaDeCargaSolicitud(), LocalDateTime.now());
            if (diasPasados > 7) {
                throw new IllegalArgumentException("El plazo de edición ha vencido. Pasaron " + diasPasados + " días.");
            }
        }

        SolicitudModificacionDTO dto = new SolicitudModificacionDTO();
        dto.setIdContribuyente(formDTO.getContribuyente_id());
        if (formDTO.getTitulo() != null && !formDTO.getTitulo().trim().isEmpty()) {
            dto.setNuevoTitulo(formDTO.getTitulo());
        }
        if (formDTO.getDescripcion() != null && !formDTO.getDescripcion().trim().isEmpty()) {
            dto.setNuevoDescripcion(formDTO.getDescripcion());
        }
        if (formDTO.getCategoria() != null && !formDTO.getCategoria().trim().isEmpty()) {
            dto.setNuevoNombreCategoria(formDTO.getCategoria());
        }
        if (formDTO.getLatitud() != null) {
            dto.setNuevoLatitud(formDTO.getLatitud());
        }
        if (formDTO.getLongitud() != null) {
            dto.setNuevoLongitud(formDTO.getLongitud());
        }
        if (formDTO.getFecha_acontecimiento() != null) {
            dto.setNuevoFechaDeAcontecimiento(formDTO.getFecha_acontecimiento());
        }

        if (formDTO.getArchivos() != null && !formDTO.getArchivos().isEmpty()) {
            long archivosNoVacios = formDTO.getArchivos().stream()
                    .filter(archivo -> archivo != null && !archivo.isEmpty())
                    .count();
            if (archivosNoVacios > 5) {
                throw new IllegalArgumentException("Solo se pueden cargar hasta 5 archivos. Se recibieron " + archivosNoVacios + " archivos.");
            }

            try {
                String solicitudJson = objectMapper.writeValueAsString(dto);

                MultipartBodyBuilder builder = new MultipartBodyBuilder();
                builder.part("solicitud", solicitudJson);

                for (MultipartFile archivo : formDTO.getArchivos()) {
                    if (archivo != null && !archivo.isEmpty()) {
                        builder.part("archivos", archivo.getResource());
                    }
                }

                String uriBase = "/solicitudes/";
                log.info("Enviando PATCH a {}{} (MULTIPART)", uriBase, idSolicitud);
                webClient2.patch()
                        .uri(uriBase + idSolicitud)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .body(BodyInserters.fromMultipartData(builder.build()))
                        .retrieve()
                        .onStatus(
                                status -> status.is4xxClientError() || status.is5xxServerError(),
                                response -> response.bodyToMono(String.class).map(body -> new RuntimeException(body))
                        )
                        .toBodilessEntity()
                        .block();
            } catch (WebClientResponseException e) {
                log.error("Error del backend al modificar solicitud ID: {} (multipart), status: {}, body: {}",
                        idSolicitud, e.getStatusCode().value(), e.getResponseBodyAsString());
                throw new RuntimeException("Error del Backend: " + e.getResponseBodyAsString());
            } catch (Exception e) {
                log.error("Error al enviar solicitud multipart para ID: {}", idSolicitud, e);
                throw new RuntimeException("Error al enviar solicitud: " + e.getMessage(), e);
            }
        } else {
            String uriBase = "/solicitudes/";
            enviarModificacionJSON(idSolicitud, dto, uriBase);
        }
    }

    private void enviarModificacionJSON(Long idSolicitud, SolicitudModificacionDTO dto, String uriBase) {
        try {
            webClient2.patch()
                    .uri(uriBase + idSolicitud)
                    .bodyValue(dto)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> response.bodyToMono(String.class).map(RuntimeException::new)
                    )
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Error del backend al modificar solicitud ID: {}, status: {}, body: {}",
                    idSolicitud, e.getStatusCode().value(), e.getResponseBodyAsString());
            throw new RuntimeException("Error del Backend: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Error al conectar con el servicio de Dinámica para solicitud ID: {}", idSolicitud, e);
            throw new RuntimeException("No se pudo conectar con el servicio de Dinámica", e);
        }
    }

    private void enviarModificacionAdminSolicitud(Long idSolicitud, Map<String, Object> adminDTO) {
        try {
            log.info("Enviando PATCH a /admin/solicitudes/{} para aceptar con modificaciones", idSolicitud);
            webClient2.patch()
                    .uri("/admin/solicitudes/" + idSolicitud)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(adminDTO)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> response.bodyToMono(String.class).map(RuntimeException::new)
                    )
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Error del backend al aceptar solicitud con modificaciones ID: {}, status: {}, body: {}",
                    idSolicitud, e.getStatusCode().value(), e.getResponseBodyAsString());
            throw new RuntimeException("Error del Backend: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Error al conectar con el servicio de Dinámica para aceptar solicitud con modificaciones ID: {}", idSolicitud, e);
            throw new RuntimeException("No se pudo conectar con el servicio de Dinámica", e);
        }
    }

    public void aceptarSolicitud(Long idSolicitud) {
        String accessToken = getAccessTokenFromSession();
        if (accessToken == null) {
            throw new RuntimeException("No hay token de acceso disponible");
        }
        
        Map<String, Object> adminDTO = new HashMap<>();
        adminDTO.put("estado", EstadoSolicitud.ACEPTADO.name());
        
        try {
            log.info("Aceptando solicitud ID: {}", idSolicitud);
            webClient2.patch()
                    .uri("/admin/solicitudes/" + idSolicitud)
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(adminDTO)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> response.bodyToMono(String.class).map(RuntimeException::new)
                    )
                    .toBodilessEntity()
                    .block();
            log.info("Solicitud {} aceptada exitosamente", idSolicitud);
        } catch (WebClientResponseException e) {
            log.error("Error del backend al aceptar solicitud ID: {}, status: {}, body: {}",
                    idSolicitud, e.getStatusCode().value(), e.getResponseBodyAsString());
            throw new RuntimeException("Error del Backend: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Error al conectar con el servicio de Dinámica para aceptar solicitud ID: {}", idSolicitud, e);
            throw new RuntimeException("No se pudo conectar con el servicio de Dinámica", e);
        }
    }

    public void rechazarSolicitud(Long idSolicitud) {
        String accessToken = getAccessTokenFromSession();
        if (accessToken == null) {
            throw new RuntimeException("No hay token de acceso disponible");
        }
        
        Map<String, Object> adminDTO = new HashMap<>();
        adminDTO.put("estado", EstadoSolicitud.RECHAZADO.name());
        
        try {
            log.info("Rechazando solicitud ID: {}", idSolicitud);
            webClient2.patch()
                    .uri("/admin/solicitudes/" + idSolicitud)
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(adminDTO)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> response.bodyToMono(String.class).map(RuntimeException::new)
                    )
                    .toBodilessEntity()
                    .block();
            log.info("Solicitud {} rechazada exitosamente", idSolicitud);
        } catch (WebClientResponseException e) {
            log.error("Error del backend al rechazar solicitud ID: {}, status: {}, body: {}",
                    idSolicitud, e.getStatusCode().value(), e.getResponseBodyAsString());
            throw new RuntimeException("Error del Backend: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Error al conectar con el servicio de Dinámica para rechazar solicitud ID: {}", idSolicitud, e);
            throw new RuntimeException("No se pudo conectar con el servicio de Dinámica", e);
        }
    }

    public void aceptarSolicitudConModificaciones(Long idSolicitud, SolicitudEdicionFormDTO formDTO) {
        String accessToken = getAccessTokenFromSession();
        if (accessToken == null) {
            throw new RuntimeException("No hay token de acceso disponible");
        }
        
        try {
            log.info("Aceptando solicitud ID: {} con modificaciones, archivos nuevos: {}, archivos a eliminar: {}", 
                    idSolicitud, 
                    formDTO.getArchivos() != null ? formDTO.getArchivos().size() : 0,
                    formDTO.getArchivos_a_eliminar() != null ? formDTO.getArchivos_a_eliminar().size() : 0);
            
            // Primero obtenemos la solicitud actual para preservar el multimedia existente
            SolicitudInputDTO solicitudActual = obtenerSolicitudPorId(idSolicitud);
            
            // Filtrar el multimedia existente eliminando los archivos marcados para eliminar
            List<ContenidoMultimediaDTO> multimediaFiltrado = solicitudActual.getContenidoMultimedia();
            boolean hayArchivosAEliminar = formDTO.getArchivos_a_eliminar() != null && !formDTO.getArchivos_a_eliminar().isEmpty();
            if (multimediaFiltrado != null && hayArchivosAEliminar) {
                log.info("Filtrando multimedia: {} archivos existentes, {} marcados para eliminar", 
                        multimediaFiltrado.size(), formDTO.getArchivos_a_eliminar().size());
                
                multimediaFiltrado = multimediaFiltrado.stream()
                    .filter(item -> !formDTO.getArchivos_a_eliminar().contains(item.getPathArchivo()))
                    .collect(Collectors.toList());
                
                log.info("Multimedia después del filtrado: {} archivos", multimediaFiltrado.size());
            }
            
            // Si hay archivos nuevos o cambios en el multimedia, necesitamos actualizar
            if ((formDTO.getArchivos() != null && !formDTO.getArchivos().isEmpty()) || hayArchivosAEliminar) {
                
                log.info("Modificando solicitud {} con archivos nuevos (preservando {} existentes después del filtrado)", 
                        idSolicitud, multimediaFiltrado != null ? multimediaFiltrado.size() : 0);
                
                SolicitudModificacionDTO dto = new SolicitudModificacionDTO();
                dto.setIdContribuyente(formDTO.getContribuyente_id());
                dto.setNuevoTitulo(formDTO.getTitulo());
                dto.setNuevoDescripcion(formDTO.getDescripcion());
                dto.setNuevoNombreCategoria(formDTO.getCategoria());
                dto.setNuevoLatitud(formDTO.getLatitud());
                dto.setNuevoLongitud(formDTO.getLongitud());
                dto.setNuevoFechaDeAcontecimiento(formDTO.getFecha_acontecimiento());
                
                // IMPORTANTE: Agregar el multimedia filtrado al DTO.
                // - Si se eliminaron todos, enviar lista vacía para que el backend borre el multimedia.
                // - Si no había multimedia previo, enviar lista vacía solo si se solicitó eliminar algo.
                if (multimediaFiltrado != null) {
                    dto.setNuevoContenidoMultimedia(multimediaFiltrado);
                    log.info("Preservando {} archivos multimedia después del filtrado", multimediaFiltrado.size());
                } else if (hayArchivosAEliminar) {
                    dto.setNuevoContenidoMultimedia(Collections.emptyList());
                    log.info("No había multimedia previo; se envía lista vacía por eliminación");
                }
                
                // Usar el método multipart que agregará los nuevos archivos a la lista existente
                if (formDTO.getArchivos() != null && !formDTO.getArchivos().isEmpty()) {
                    enviarModificacionMultipart(idSolicitud, dto, formDTO.getArchivos(), "/solicitudes/");
                } else {
                    // Si solo se eliminaron archivos pero no se agregaron nuevos, enviar JSON
                    enviarModificacionJSON(idSolicitud, dto, "/solicitudes/");
                }
            }
            
            // Luego aceptamos la solicitud con el endpoint de admin
            Map<String, Object> adminDTO = construirMapAdminDTO(formDTO, EstadoSolicitud.ACEPTADO_MODIFICADO);
            
            webClient2.patch()
                    .uri("/admin/solicitudes/" + idSolicitud)
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(adminDTO)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> response.bodyToMono(String.class).map(RuntimeException::new)
                    )
                    .toBodilessEntity()
                    .block();
            
            log.info("Solicitud {} aceptada con modificaciones exitosamente", idSolicitud);
        } catch (WebClientResponseException e) {
            log.error("Error del backend al aceptar solicitud con modificaciones ID: {}, status: {}, body: {}",
                    idSolicitud, e.getStatusCode().value(), e.getResponseBodyAsString());
            throw new RuntimeException("Error del Backend: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Error al conectar con el servicio de Dinámica para aceptar solicitud con modificaciones ID: {}", idSolicitud, e);
            throw new RuntimeException("No se pudo conectar con el servicio de Dinámica", e);
        }
    }

    public SolicitudInputDTO obtenerSolicitudPorId(Long idSolicitud) {
        try {
            String accessToken = getAccessTokenFromSession();
            if (accessToken == null) {
                throw new RuntimeException("No hay token de acceso disponible");
            }
            
            return webClient2.get()
                    .uri("/solicitudes/" + idSolicitud)
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(SolicitudInputDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Error del backend al obtener solicitud ID: {}, status: {}, body: {}",
                    idSolicitud, e.getStatusCode().value(), e.getResponseBodyAsString());
            throw new RuntimeException("Error del Backend: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Error al conectar con el servicio de Dinámica para obtener solicitud ID: {}", idSolicitud, e);
            throw new RuntimeException("No se pudo conectar con el servicio de Dinámica", e);
        }
    }

      private Map<String, Object> construirMapAdminDTO(SolicitudEdicionFormDTO formDTO, EstadoSolicitud estado) {
        Map<String, Object> adminDTO = new HashMap<>();
        adminDTO.put("estado", estado.name());

        if (formDTO.getTitulo() != null && !formDTO.getTitulo().trim().isEmpty()) {
          adminDTO.put("titulo", formDTO.getTitulo());
        }
        if (formDTO.getDescripcion() != null && !formDTO.getDescripcion().trim().isEmpty()) {
          adminDTO.put("descripcion", formDTO.getDescripcion());
        }
        if (formDTO.getCategoria() != null && !formDTO.getCategoria().trim().isEmpty()) {
          adminDTO.put("categoria", formDTO.getCategoria());
        }
        if (formDTO.getLatitud() != null) {
          adminDTO.put("latitud", formDTO.getLatitud());
        }
        if (formDTO.getLongitud() != null) {
          adminDTO.put("longitud", formDTO.getLongitud());
        }
        if (formDTO.getFecha_acontecimiento() != null) {
          adminDTO.put("fecha_acontecimiento", formDTO.getFecha_acontecimiento().toString());
        }

        return adminDTO;
      }

      private void enviarModificacionSolicitud(Long idSolicitud, SolicitudModificacionDTO dto,
          List<MultipartFile> archivos, String uriBase) {
        if (archivos != null && !archivos.isEmpty()) {
          enviarModificacionMultipart(idSolicitud, dto, archivos, uriBase);
        } else {
          enviarModificacionJSON(idSolicitud, dto, uriBase);
        }
      }

      private void enviarModificacionMultipart(Long idSolicitud, SolicitudModificacionDTO dto,
          List<MultipartFile> archivos, String uriBase) {
        try {
          String accessToken = getAccessTokenFromSession();
          if (accessToken == null) {
            throw new RuntimeException("No hay token de acceso disponible");
          }
          
          String solicitudJson = objectMapper.writeValueAsString(dto);

          MultipartBodyBuilder builder = new MultipartBodyBuilder();
          builder.part("solicitud", solicitudJson);

          for (MultipartFile archivo : archivos) {
            if (archivo != null && !archivo.isEmpty()) {
              builder.part("archivos", archivo.getResource());
            }
          }

          log.info("Enviando PATCH a {}{} (MULTIPART) con {} archivos", uriBase, idSolicitud, archivos.size());
          webClient2.patch()
              .uri(uriBase + idSolicitud)
              .header("Authorization", "Bearer " + accessToken)
              .contentType(MediaType.MULTIPART_FORM_DATA)
              .body(BodyInserters.fromMultipartData(builder.build()))
              .retrieve()
              .onStatus(
                  status -> status.is4xxClientError() || status.is5xxServerError(),
                  response -> response.bodyToMono(String.class).map(RuntimeException::new)
              )
              .toBodilessEntity()
              .block();
        } catch (WebClientResponseException e) {
          log.error("Error del backend al modificar solicitud ID: {} (multipart), status: {}, body: {}",
              idSolicitud, e.getStatusCode().value(), e.getResponseBodyAsString());
          throw new RuntimeException("Error del Backend: " + e.getResponseBodyAsString());
        } catch (Exception e) {
          log.error("Error al enviar solicitud multipart para ID: {}", idSolicitud, e);
          throw new RuntimeException("Error al enviar solicitud: " + e.getMessage(), e);
        }
      }
}




