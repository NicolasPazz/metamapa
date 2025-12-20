package ar.utn.ba.ddsi.frontend.service;

import ar.utn.ba.ddsi.frontend.models.dtos.EstadisticasCompletasDTO;
import lombok.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class EstadisticasService {
    private final WebClient webClient;

    public EstadisticasService(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://localhost:8085/api").build();
    }

    public List<EstadisticasCompletasDTO> obtenerEstadisticas() {
        return webClient.get().uri("/estadisticas").retrieve().bodyToFlux(EstadisticasCompletasDTO.class).collectList().block();
    }
}
