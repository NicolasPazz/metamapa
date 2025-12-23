package ar.utn.ba.ddsi.frontend.service;

import ar.utn.ba.ddsi.frontend.models.dtos.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class AdminService {
    private final WebClient webClientAgregador;
    private final WebClient webClientEstatica;
    private final WebClient webClientAgregadorBase;
    private final WebClient webClientEstadistica;

    public AdminService(WebClient.Builder builder,
                       @Value("${app.api.agregador.base-url:http://localhost:8084/api}") String agregadorBaseUrl,
                       @Value("${app.api.estatica.base-url:http://localhost:8082/api}") String estaticaBaseUrl,
                        @Value("${app.api.estadistica.base-url:http://localhost:8085/api/estadisticas}") String estadisticaBaseUrl
                        ) {
        this.webClientAgregador = builder.baseUrl(agregadorBaseUrl + "/admin").build();
        this.webClientAgregadorBase = builder.baseUrl(agregadorBaseUrl).build();
        this.webClientEstatica = builder.baseUrl(estaticaBaseUrl).build();
        this.webClientEstadistica = builder.baseUrl(estadisticaBaseUrl).build();
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

    public List<SolicitudDTO> getAllSolicitudes() {
        String accessToken = getAccessTokenFromSession();
        if (accessToken == null) {
            throw new RuntimeException("No hay token de acceso disponible");
        }
        return webClientAgregador.get()
                .uri("/solicitudes")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToFlux(SolicitudDTO.class)
                .collectList()
                .block();
    }

    public void actualizarSolicitud(Long id, String estado) {
        String accessToken = getAccessTokenFromSession();
        if (accessToken == null) {
            throw new RuntimeException("No hay token de acceso disponible");
        }
        
        SolicitudOutputDTO dtoBack = new SolicitudOutputDTO();

        if(Objects.equals(estado, "RECHAZADO")) dtoBack.setEstado(EstadoSolicitud.RECHAZADO);
        else dtoBack.setEstado(EstadoSolicitud.ACEPTADO);

        webClientAgregador.patch()
                .uri("/solicitudes/" + id)
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(dtoBack)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
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
        String accessToken = getAccessTokenFromSession();
        if (accessToken == null) {
            throw new RuntimeException("No hay token de acceso disponible");
        }
        
        webClientAgregador
                .post()
                .uri("/colecciones")
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void actualizarColeccion(CrearColeccionDTO dto, String handle) {
        String accessToken = getAccessTokenFromSession();
        if (accessToken == null) {
            throw new RuntimeException("No hay token de acceso disponible");
        }
        
        webClientAgregador.patch()
                .uri("/colecciones/" + handle)
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void agregarEtiquetasAHecho(Long hechoId, List<String> etiquetas) {
        if (hechoId == null) {
            throw new IllegalArgumentException("El ID del hecho no puede ser nulo");
        }
        if (etiquetas == null || etiquetas.isEmpty()) {
            throw new IllegalArgumentException("Debe proporcionar al menos una etiqueta");
        }

        webClientAgregador.patch()
                .uri("/hechos/"+ hechoId + "/etiquetas")
                .bodyValue(etiquetas)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
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
 // /api/estadisticas
        log.info("Actualización de estadísticas solicitada");

    }

}
