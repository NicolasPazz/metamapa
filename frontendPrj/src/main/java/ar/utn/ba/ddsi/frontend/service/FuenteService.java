package ar.utn.ba.ddsi.frontend.service;

import ar.utn.ba.ddsi.frontend.models.dtos.FuenteDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class FuenteService {
    private final WebClient webclient;

    public FuenteService(WebClient.Builder builder) {
        this.webclient = builder.baseUrl("http://localhost:8084/api").build();
    }

    public List<FuenteDTO> getAllFuentes() {
        return webclient.get().uri("/fuentes").retrieve().bodyToFlux(FuenteDTO.class).collectList().block();
    }
}
