package ar.utn.ba.ddsi.frontend.service;

import ar.utn.ba.ddsi.frontend.models.dtos.EstadisticasCompletasDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class EstadisticasService {
    private final WebClient webClient;

    public EstadisticasService(
            WebClient.Builder builder,
            @Value("${app.api.estadistica.base-url:http://localhost:8085/api/estadisticas}") String estadisticaBaseUrl
    ) {
        this.webClient = builder.baseUrl(estadisticaBaseUrl).build();
    }

    public EstadisticasCompletasDTO obtenerEstadisticas() {
        return webClient.get()
                .uri("")
                .retrieve()
                .bodyToMono(EstadisticasCompletasDTO.class)
                .block();
    }
}
