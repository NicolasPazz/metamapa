package ar.utn.ba.ddsi.frontend.service;

import ar.utn.ba.ddsi.frontend.models.dtos.SolicitudDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class SolicitudService {
    final private WebClient webClient;

    public SolicitudService(WebClient.Builder builder,
                           @Value("${app.api.agregador.base-url:http://localhost:8084/api}") String agregadorBaseUrl) {
        this.webClient = builder.baseUrl(agregadorBaseUrl).build();
    }

    public SolicitudDTO crearSolicitud(SolicitudDTO solicitud) {
        return webClient.post()
                .uri("/solicitudes")
                .bodyValue(solicitud)
                .retrieve()
                .bodyToMono(SolicitudDTO.class)
                .block();
    }


}
