package ar.utn.ba.ddsi.frontend.service;

import ar.utn.ba.ddsi.frontend.models.dtos.ColeccionDTO;
import ar.utn.ba.ddsi.frontend.models.dtos.HechoDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class ColeccionService {
    private final WebClient webClient;

    public ColeccionService(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://localhost:8084/api").build();
    }

    public List<ColeccionDTO> getAllColecciones() {
        return webClient.get().uri("/colecciones").retrieve().bodyToFlux(ColeccionDTO.class).collectList().block();
    }

    public List<HechoDTO> getHechosColeccionByHandle(String handle) {
        return webClient.get().uri("/colecciones/"+handle+"/hechos").retrieve().bodyToFlux(HechoDTO.class).collectList().block();
    }
}
