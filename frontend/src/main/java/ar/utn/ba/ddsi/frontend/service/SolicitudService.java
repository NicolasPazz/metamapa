package ar.utn.ba.ddsi.frontend.service;

import ar.utn.ba.ddsi.frontend.models.dtos.*;
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

        // Obtener la solicitud para verificar su estado
        SolicitudInputDTO solicitud = this.buscarSolicitudPorId(idSolicitud);

        // Validar que pasaron <= 7 días desde la creación
        if (solicitud.getFechaDeCargaSolicitud() != null) {
            long diasPasados = ChronoUnit.DAYS.between(solicitud.getFechaDeCargaSolicitud(), LocalDateTime.now());
            if (diasPasados > 7) {
                throw new IllegalArgumentException("El plazo de edición ha vencido. Pasaron " + diasPasados + " días.");
            }
        }

        // Construir el DTO para enviar al backend
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

        // Si hay archivos, enviar como multipart (igual que en la creación)
        if (formDTO.getArchivos() != null && !formDTO.getArchivos().isEmpty()) {
            try {
                // Serializar el objeto a JSON string manualmente (el backend espera String)
                String solicitudJson = objectMapper.writeValueAsString(dto);
                log.debug("SolicitudService.procesarSolicitudEdicionConMultipart - JSON serializado: {}", solicitudJson);

                // Construir multipart igual que en la creación
                MultipartBodyBuilder builder = new MultipartBodyBuilder();

                // Agregar datos de la solicitud como parte JSON (String)
                builder.part("solicitud", solicitudJson);

                // Agregar cada archivo multimedia
                for (MultipartFile archivo : formDTO.getArchivos()) {
                    if (archivo != null && !archivo.isEmpty()) {
                        log.debug("SolicitudService.procesarSolicitudEdicionConMultipart - Agregando archivo: {} ({} bytes)",
                                archivo.getOriginalFilename(), archivo.getSize());
                        builder.part("archivos", archivo.getResource());
                    }
                }

                log.info("SolicitudService.procesarSolicitudEdicionConMultipart - Enviando PATCH a /solicitudes/{} (MULTIPART)", idSolicitud);
                webClient2.patch()
                        .uri("/solicitudes/" + idSolicitud)
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
        } else {
            // Si no hay archivos, enviar como JSON
            procesarSolicitudEdicion(idSolicitud, dto);
        }
    }

    @Transactional
    public void procesarSolicitudEdicion(Long idSolicitud, SolicitudModificacionDTO dto) {
        log.debug("Procesando edición de solicitud ID: {}", idSolicitud);
        // Obtener la solicitud para verificar su estado
        SolicitudInputDTO solicitud =this.buscarSolicitudPorId(idSolicitud);
        // Validar que pasaron <= 7 días desde la creación
        if (solicitud.getFechaDeCargaSolicitud() != null) {
            long diasPasados = ChronoUnit.DAYS.between(solicitud.getFechaDeCargaSolicitud(), LocalDateTime.now());
            if (diasPasados > 7) {
                throw new IllegalArgumentException("El plazo de edición ha vencido. Pasaron " + diasPasados + " días.");
            }
        }

        // Si la solicitud está en ACEPTADO o ACEPTADO_MODIFICADO, 
        // el hecho se ocultará automáticamente cuando se vuelva a aceptar la solicitud
        // (esto se maneja en el backend de dinámica al cambiar el estado)

        // Modificar la solicitud en dinámica
        try {
            log.debug("Enviando PATCH a /solicitudes/{} para solicitud ID: {}", idSolicitud, idSolicitud);
            webClient2.patch()
                    .uri("/solicitudes/" + idSolicitud)
                    .bodyValue(dto)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> response.bodyToMono(String.class).map(body -> new RuntimeException(body))
                    )
                    .toBodilessEntity()
                    .block();
            log.debug("PATCH exitoso para solicitud ID: {}", idSolicitud);

        } catch (WebClientResponseException e) {
            log.error("Error del backend al modificar solicitud ID: {}, status: {}, body: {}",
                    idSolicitud, e.getStatusCode().value(), e.getResponseBodyAsString());
            throw new RuntimeException("Error del Backend: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Error al conectar con el servicio de Dinámica para solicitud ID: {}", idSolicitud, e);
            throw new RuntimeException("No se pudo conectar con el servicio de Dinámica", e);
        }
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
}




