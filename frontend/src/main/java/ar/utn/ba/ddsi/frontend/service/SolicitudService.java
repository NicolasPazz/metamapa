package ar.utn.ba.ddsi.frontend.service;

import ar.utn.ba.ddsi.frontend.models.dtos.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class SolicitudService {

    final private HechoService hechoService;
    final private WebClient webClient;
    final private WebClient webClient2;

    public SolicitudService(WebClient.Builder builder, HechoService hechoService,
                           @Value("${app.api.agregador.base-url:http://localhost:8084/api}") String agregadorBaseUrl,
                            @Value("${app.api.dinamica.base-url:http://localhost:8081/api}") String dinamicaBaseUrl) {
        this.webClient = builder.baseUrl(agregadorBaseUrl).build();
        this.webClient2= builder.baseUrl(dinamicaBaseUrl).build();
        this.hechoService = hechoService;
    }
    public List<SolicitudInputDTO> buscarTodasLasSolicitudes() {
        try {
            return webClient2.get()
                    // La URL final será: http://localhost:8081/api/admin/solicitudes
                    // Ajusta "/admin/solicitudes" según el @RequestMapping de tu controller backend
                    .uri("/admin/solicitudes")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<SolicitudInputDTO>>() {})
                    .block(); // Bloqueamos para esperar la respuesta sincrónica

        } catch (Exception e) {
            e.printStackTrace();
            // En caso de error, devolvemos una lista vacía para no romper la pantalla admin
            return Collections.emptyList();
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


    @Transactional
    public void procesarSolicitudEdicion(Long idSolicitud, SolicitudModificacionDTO dto) {
        HechoDTO hecho = Optional.ofNullable(hechoService.getHechoById(idSolicitud))
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el hecho con ID: " + idSolicitud));

        if (hecho.getFechaDeCarga() != null) {
            long diasPasados = ChronoUnit.DAYS.between(hecho.getFechaDeCarga(), LocalDateTime.now());
            if (diasPasados > 5000) {
                throw new IllegalArgumentException("El plazo de edición ha vencido. Pasaron " + diasPasados + " días.");

            }
        }
        try {
            webClient2.patch()
                    .uri("/solicitudes/" + idSolicitud) //
                    .bodyValue(dto)
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
            throw new RuntimeException("No se pudo conectar con el servicio de Dinámica", e);
        }
    }


    public void cambiarEstado(Long idSolicitud, String estado) {
        String accion = "aceptar"; // Por defecto
        if ("RECHAZADO".equalsIgnoreCase(estado)) {
            accion = "rechazar";
        }
        try {
            webClient2.patch()
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




