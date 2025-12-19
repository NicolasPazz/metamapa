package ar.utn.ba.ddsi.frontend.service;

import ar.utn.ba.ddsi.frontend.models.dtos.HechoDTO;
import ar.utn.ba.ddsi.frontend.models.dtos.SolicitudDTO;
import ar.utn.ba.ddsi.frontend.models.dtos.SolicitudModificarHechoDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    public SolicitudDTO crearSolicitud(SolicitudDTO solicitud) {
        return webClient.post()
                .uri("/solicitudes")
                .bodyValue(solicitud)
                .retrieve()
                .bodyToMono(SolicitudDTO.class)
                .block();
    }
    @Transactional // Importante para asegurar la integridad de datos
    public void procesarSolicitudEdicion(Long idHecho, SolicitudModificarHechoDTO dto) {


        HechoDTO hecho = Optional.ofNullable(hechoService.getHechoById(idHecho))
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el hecho con ID: " + idHecho));

        if (hecho.getFechaDeCarga() != null) {
            long diasPasados = ChronoUnit.DAYS.between(hecho.getFechaDeCarga(), LocalDateTime.now());
            if (diasPasados > 7) {
                throw new IllegalArgumentException("El plazo de edición ha vencido. Pasaron " + diasPasados + " días.");
            }
        }
        try {
            webClient2.patch() //TODO: Patch al solicitudes, pero tendria que enviar ID de solicitud, pero no se de donde sacarla
                    .uri("/solicitudes/" + idHecho) //
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
    }




