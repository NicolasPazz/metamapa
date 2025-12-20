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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
                Long userId = userIdObj instanceof Long ? (Long) userIdObj : Long.valueOf(userIdObj.toString());
                return userId;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public List<SolicitudInputDTO> buscarTodasLasSolicitudes() {
        try {
            return webClient2.get()
                    // La URL final será: http://localhost:8081/api/admin/solicitudes
                    // Ajusta "/admin/solicitudes" según el @RequestMapping de tu controller backend
                    .uri("/admin/solicitudes")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<SolicitudInputDTO>>() {
                    })
                    .block(); // Bloqueamos para esperar la respuesta sincrónica

        } catch (Exception e) {
            log.error("Error al buscar todas las solicitudes: {}", e.getMessage(), e);
            // En caso de error, devolvemos una lista vacía para no romper la pantalla admin
            return Collections.emptyList();
        }
    }

    public List<SolicitudInputDTO> obtenerSolicitudesPorContribuyente(Long idContribuyente) {
        try {
            return webClient2.get()
                    .uri("/solicitudes/contribuyente/" + idContribuyente)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<SolicitudInputDTO>>() {
                    })
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
                            response -> response.bodyToMono(String.class).map(body -> new RuntimeException(body))
                    )
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Error del Backend: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException("No se pudo conectar con el servicio de Agregador", e);
        }
    }

    public SolicitudInputDTO buscarSolicitudPorId(Long idSolicitud) {
        // Obtener la solicitud para verificar su estado
        // Obtener la solicitud para verificar su estado
        SolicitudInputDTO solicitud = webClient2.get()
                .uri("/solicitudes/" + idSolicitud)
                .retrieve()
                .bodyToMono(SolicitudInputDTO.class)
                .block();
        log.debug("Solicitud obtenida para ID: {}, encontrada: {}", idSolicitud, solicitud != null);

        if (solicitud == null) {
            throw new IllegalArgumentException("No se encontró la solicitud con ID: " + idSolicitud);
        }
        return solicitud;
    }


    @Transactional
    public void procesarSolicitudEdicionConMultipart(Long idSolicitud, SolicitudEdicionFormDTO formDTO) {
        log.debug("Procesando edición de solicitud ID: {} con multipart", idSolicitud);
        
        validarPlazoEdicion(idSolicitud);
        
        SolicitudModificacionDTO dto = construirSolicitudModificacionDTO(formDTO);
        
        enviarModificacionSolicitud(idSolicitud, dto, formDTO.getArchivos(), "/solicitudes/");
    }

    @Transactional
    public void procesarSolicitudEdicion(Long idSolicitud, SolicitudModificacionDTO dto) {
        log.debug("Procesando edición de solicitud ID: {}", idSolicitud);
        validarPlazoEdicion(idSolicitud);
        enviarModificacionSolicitud(idSolicitud, dto, null, "/solicitudes/");
    }




    public void cambiarEstado(Long idSolicitud, String estado) {
        String accion = "aceptar"; // Por defecto
        if ("RECHAZADO".equalsIgnoreCase(estado)) {
            accion = "rechazar";
        }
        try {
            webClient2.post()
                    .uri("/solicitudes/" + idSolicitud + "/" + accion)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> response.bodyToMono(String.class).map(body -> new RuntimeException(body))
                    )
                    .toBodilessEntity() // Indicamos que no esperamos un cuerpo de respuesta, solo el status OK
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Error del Backend: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException("No se pudo conectar con el servicio de Dinámica", e);
        }
    }

    @Transactional
    public void procesarAceptarConModificaciones(Long idSolicitud, SolicitudEdicionFormDTO formDTO) {
        log.debug("Procesando aceptación con modificaciones de solicitud ID: {}", idSolicitud);
        
        java.util.Map<String, Object> adminDTO = construirMapAdminDTO(formDTO, EstadoSolicitud.ACEPTADO_MODIFICADO);
        
        enviarModificacionAdminSolicitud(idSolicitud, adminDTO);
    }

    // ========== MÉTODOS PRIVADOS HELPER ==========

    /**
     * Valida que no hayan pasado más de 7 días desde la creación de la solicitud
     */
    private void validarPlazoEdicion(Long idSolicitud) {
        SolicitudInputDTO solicitud = this.buscarSolicitudPorId(idSolicitud);
        if (solicitud.getFechaDeCargaSolicitud() != null) {
            long diasPasados = ChronoUnit.DAYS.between(solicitud.getFechaDeCargaSolicitud(), LocalDateTime.now());
            if (diasPasados > 7) {
                throw new IllegalArgumentException("El plazo de edición ha vencido. Pasaron " + diasPasados + " días.");
            }
        }
    }

    /**
     * Construye un SolicitudModificacionDTO a partir de un SolicitudEdicionFormDTO
     */
    private SolicitudModificacionDTO construirSolicitudModificacionDTO(SolicitudEdicionFormDTO formDTO) {
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
        
        return dto;
    }

    /**
     * Construye un Map para el endpoint de administración con el estado especificado
     */
    private java.util.Map<String, Object> construirMapAdminDTO(SolicitudEdicionFormDTO formDTO, EstadoSolicitud estado) {
        java.util.Map<String, Object> adminDTO = new java.util.HashMap<>();
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

    /**
     * Envía la modificación de solicitud al backend (soporta multipart y JSON)
     */
    private void enviarModificacionSolicitud(Long idSolicitud, SolicitudModificacionDTO dto, 
                                             List<MultipartFile> archivos, String uriBase) {
        if (archivos != null && !archivos.isEmpty()) {
            enviarModificacionMultipart(idSolicitud, dto, archivos, uriBase);
        } else {
            enviarModificacionJSON(idSolicitud, dto, uriBase);
        }
    }

    /**
     * Envía modificación con archivos multipart
     */
    private void enviarModificacionMultipart(Long idSolicitud, SolicitudModificacionDTO dto, 
                                             List<MultipartFile> archivos, String uriBase) {
        try {
            String solicitudJson = objectMapper.writeValueAsString(dto);
            log.debug("JSON serializado para solicitud ID: {}", idSolicitud);

            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("solicitud", solicitudJson);

            for (MultipartFile archivo : archivos) {
                if (archivo != null && !archivo.isEmpty()) {
                    log.debug("Agregando archivo: {} ({} bytes)", archivo.getOriginalFilename(), archivo.getSize());
                    builder.part("archivos", archivo.getResource());
                }
            }

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
            log.debug("PATCH multipart exitoso para solicitud ID: {}", idSolicitud);
        } catch (WebClientResponseException e) {
            log.error("Error del backend al modificar solicitud ID: {} (multipart), status: {}, body: {}",
                    idSolicitud, e.getStatusCode().value(), e.getResponseBodyAsString());
            throw new RuntimeException("Error del Backend: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Error al enviar solicitud multipart para ID: {}", idSolicitud, e);
            throw new RuntimeException("Error al enviar solicitud: " + e.getMessage(), e);
        }
    }

    /**
     * Envía modificación como JSON
     */
    private void enviarModificacionJSON(Long idSolicitud, SolicitudModificacionDTO dto, String uriBase) {
        try {
            log.debug("Enviando PATCH a {}{} (JSON)", uriBase, idSolicitud);
            webClient2.patch()
                    .uri(uriBase + idSolicitud)
                    .bodyValue(dto)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> response.bodyToMono(String.class).map(body -> new RuntimeException(body))
                    )
                    .toBodilessEntity()
                    .block();
            log.debug("PATCH JSON exitoso para solicitud ID: {}", idSolicitud);
        } catch (WebClientResponseException e) {
            log.error("Error del backend al modificar solicitud ID: {}, status: {}, body: {}",
                    idSolicitud, e.getStatusCode().value(), e.getResponseBodyAsString());
            throw new RuntimeException("Error del Backend: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Error al conectar con el servicio de Dinámica para solicitud ID: {}", idSolicitud, e);
            throw new RuntimeException("No se pudo conectar con el servicio de Dinámica", e);
        }
    }

    /**
     * Envía modificación al endpoint de administración (solo JSON)
     */
    private void enviarModificacionAdminSolicitud(Long idSolicitud, java.util.Map<String, Object> adminDTO) {
        try {
            log.info("Enviando PATCH a /admin/solicitudes/{} para aceptar con modificaciones", idSolicitud);
            webClient2.patch()
                    .uri("/admin/solicitudes/" + idSolicitud)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(adminDTO)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> response.bodyToMono(String.class).map(body -> new RuntimeException(body))
                    )
                    .toBodilessEntity()
                    .block();
            log.debug("PATCH exitoso para aceptar solicitud {} con modificaciones", idSolicitud);
        } catch (WebClientResponseException e) {
            log.error("Error del backend al aceptar solicitud con modificaciones ID: {}, status: {}, body: {}",
                    idSolicitud, e.getStatusCode().value(), e.getResponseBodyAsString());
            throw new RuntimeException("Error del Backend: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Error al conectar con el servicio de Dinámica para aceptar solicitud con modificaciones ID: {}", idSolicitud, e);
            throw new RuntimeException("No se pudo conectar con el servicio de Dinámica", e);
        }
    }
}




