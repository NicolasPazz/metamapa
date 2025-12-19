package ar.utn.ba.ddsi.frontend.service;

import ar.utn.ba.ddsi.frontend.models.dtos.FuenteDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class FuenteService {
    private final WebClient webclient;

    public FuenteService(WebClient.Builder builder,
                        @Value("${app.api.agregador.base-url:http://localhost:8084/api}") String agregadorBaseUrl) {
        this.webclient = builder.baseUrl(agregadorBaseUrl).build();
    }

    public List<FuenteDTO> getAllFuentes() {
        return webclient.get().uri("/fuentes").retrieve().bodyToFlux(FuenteDTO.class).collectList().block();
    }
}
