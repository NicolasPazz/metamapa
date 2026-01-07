package ar.utn.ba.ddsi.frontend.service;

import ar.utn.ba.ddsi.frontend.models.dtos.*;
import ar.utn.ba.ddsi.frontend.dto.AuthResponseDTO;
import ar.utn.ba.ddsi.frontend.dto.RefreshTokenDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import reactor.core.publisher.Mono;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@Slf4j
@Service
public class AdminService {
    private final WebClient webClientAgregador;
    private final WebClient webClientEstatica;
    private final WebClient webClientAgregadorBase;
    private final WebClient webClientEstadistica;
    private final WebClient authWebClient;
    private final String authServiceUrl;

    public AdminService(WebClient.Builder builder,
                       @Value("${app.api.agregador.base-url:http://localhost:8084/api}") String agregadorBaseUrl,
                       @Value("${app.api.estatica.base-url:http://localhost:8082/api}") String estaticaBaseUrl,
                        @Value("${app.api.estadistica.base-url:http://localhost:8085/api/estadisticas}") String estadisticaBaseUrl,
                        @Value("${app.api.users.base-url:http://localhost:8087/api}") String authServiceUrl
                        ) {
        this.webClientAgregador = builder.baseUrl(agregadorBaseUrl + "/admin").build();
        this.webClientAgregadorBase = builder.baseUrl(agregadorBaseUrl).build();
        this.webClientEstatica = builder.baseUrl(estaticaBaseUrl).build();
        this.webClientEstadistica = builder.baseUrl(estadisticaBaseUrl).build();
        this.authWebClient = builder.build();
        this.authServiceUrl = authServiceUrl;
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

    private String getRefreshTokenFromSession() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            return (String) request.getSession().getAttribute("refreshToken");
        } catch (Exception e) {
            return null;
        }
    }

    private void updateTokensInSession(String accessToken, String refreshToken) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            request.getSession().setAttribute("accessToken", accessToken);
            if (refreshToken != null) {
                request.getSession().setAttribute("refreshToken", refreshToken);
            }
        } catch (Exception e) {
            log.error("[AdminService] Error al actualizar tokens en sesión: {}", e.getMessage());
        }
    }

    private AuthResponseDTO refreshToken(String refreshToken) {
        try {
            RefreshTokenDTO refreshRequest = RefreshTokenDTO.builder()
                    .refreshToken(refreshToken)
                    .build();

            AuthResponseDTO response = authWebClient
                    .post()
                    .uri(authServiceUrl + "/auth/refresh")
                    .bodyValue(refreshRequest)
                    .retrieve()
                    .bodyToMono(AuthResponseDTO.class)
                    .block();

            if (response == null) {
                throw new RuntimeException("Error al refrescar token: respuesta nula");
            }
            updateTokensInSession(response.getAccessToken(), response.getRefreshToken());
            log.info("[AdminService] Token refrescado exitosamente");
            return response;
        } catch (Exception e) {
            log.error("[AdminService] Error al refrescar token: {}", e.getMessage());
            throw new RuntimeException("Error al refrescar token: " + e.getMessage(), e);
        }
    }

    private <T> T executeWithTokenRetry(TokenApiCall<T> apiCall) {
        String accessToken = getAccessTokenFromSession();
        String refreshToken = getRefreshTokenFromSession();

        if (accessToken == null) {
            throw new RuntimeException("No hay token de acceso disponible");
        }

        try {
            return apiCall.execute(accessToken);
        } catch (WebClientResponseException e) {
            if ((e.getStatusCode() == HttpStatus.UNAUTHORIZED || e.getStatusCode() == HttpStatus.FORBIDDEN) && refreshToken != null) {
                try {
                    log.info("[AdminService] Token expirado, intentando refrescar...");
                    AuthResponseDTO newTokens = refreshToken(refreshToken);
                    
                    if (newTokens.getAccessToken() == null) {
                        throw new RuntimeException("Error al refrescar token: respuesta inválida");
                    }
                    
                    log.info("[AdminService] Reintentando petición con nuevo token");
                    return apiCall.execute(newTokens.getAccessToken());
                } catch (Exception refreshError) {
                    log.error("[AdminService] Error al refrescar token y reintentar: {}", refreshError.getMessage());
                    throw new RuntimeException("Error al refrescar token y reintentar: " + refreshError.getMessage(), refreshError);
                }
            }
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error en llamada al API: " + e.getMessage(), e);
        }
    }

    @FunctionalInterface
    private interface TokenApiCall<T> {
        T execute(String accessToken) throws Exception;
    }

    public List<SolicitudDTO> getAllSolicitudes() {
        return executeWithTokenRetry(accessToken -> {
            return webClientAgregador.get()
                    .uri("/solicitudes")
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .bodyToFlux(SolicitudDTO.class)
                    .collectList()
                    .block();
        });
    }

    public void actualizarSolicitud(Long id, String estado) {
        // Enviar el estado como un Map con el valor correcto para el backend agregador
        // El backend espera: ACEPTADA o RECHAZADA
        java.util.Map<String, String> body = new java.util.HashMap<>();
        body.put("estado", estado); // ACEPTADA o RECHAZADA desde el HTML

        log.info("[AdminService] - Actualizando solicitud {} con estado: {}", id, estado);
        
        String response = executeWithTokenRetry(accessToken -> {
            return webClientAgregador.patch()
                    .uri("/solicitudes/" + id)
                    .header("Authorization", "Bearer " + accessToken)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        });
        
        log.info("[AdminService] - Respuesta del backend: {}", response);
    }

    public void enviarArchivoAlBackend(MultipartFile archivo) {
        if (archivo.isEmpty()) {
            throw new RuntimeException("El archivo está vacío");
        }
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("archivo", archivo.getResource());

        webClientEstatica.post()
                .uri("/csv")
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public void crearColeccion(CrearColeccionDTO dto) {
        try {
            executeWithTokenRetry(accessToken -> {
                webClientAgregador
                        .post()
                        .uri("/colecciones")
                        .header("Authorization", "Bearer " + accessToken)
                        .bodyValue(dto)
                        .retrieve()
                        .onStatus(status -> status.isError(), response -> {
                            log.error("[AdminService] ERROR - Respuesta del backend con error: {}", response.statusCode());
                            return response.bodyToMono(String.class)
                                    .doOnNext(body -> log.error("[AdminService] ERROR - Cuerpo de respuesta: {}", body))
                                    .flatMap(body -> Mono.error(new RuntimeException("Error del backend: " + response.statusCode() + " - " + body)));
                        })
                        .bodyToMono(Void.class)
                        .doOnSuccess(v -> log.info("[AdminService] Colección creada exitosamente"))
                        .doOnError(e -> {
                            if (e instanceof WebClientResponseException) {
                                WebClientResponseException ex = (WebClientResponseException) e;
                                log.error("[AdminService] ERROR - Error HTTP {}: {}", ex.getStatusCode(), ex.getResponseBodyAsString());
                            } else {
                                log.error("[AdminService] ERROR - Error al crear colección: {}", e.getMessage(), e);
                            }
                        })
                        .block();
                return null;
            });
        } catch (Exception e) {
            log.error("[AdminService] ERROR - Excepción al crear colección: {}", e.getMessage(), e);
            throw e;
        }
    }

    public void actualizarColeccion(CrearColeccionDTO dto, String handle) {
        executeWithTokenRetry(accessToken -> {
            webClientAgregador.patch()
                    .uri("/colecciones/" + handle)
                    .header("Authorization", "Bearer " + accessToken)
                    .bodyValue(dto)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            return null;
        });
    }

    public void agregarEtiquetasAHecho(Long hechoId, List<String> etiquetas) {
        if (hechoId == null) {
            throw new IllegalArgumentException("El ID del hecho no puede ser nulo");
        }
        if (etiquetas == null || etiquetas.isEmpty()) {
            throw new IllegalArgumentException("Debe proporcionar al menos una etiqueta");
        }

        executeWithTokenRetry(accessToken -> {
            webClientAgregador.patch()
                    .uri("/hechos/"+ hechoId + "/etiquetas")
                    .header("Authorization", "Bearer " + accessToken)
                    .bodyValue(etiquetas)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            return null;
        });
    }

    public void ejecutarSchedulerAgregador() {
        webClientAgregadorBase.post()
                .uri("/front/ejecutar-scheduler")
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void actualizarEstadisticas() {
        webClientEstadistica.post()
                .uri("/recalcular")
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public String exportarEstadisticasCSV() {
        log.info("[AdminService] - Exportando estadísticas a CSV");
        return webClientEstadistica.get()
                .uri("/export/csv")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

}
