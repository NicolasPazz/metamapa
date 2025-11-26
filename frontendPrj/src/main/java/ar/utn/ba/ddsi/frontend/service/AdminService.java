package ar.utn.ba.ddsi.frontend.service;

import ar.utn.ba.ddsi.frontend.models.dtos.EstadoSolicitud;
import ar.utn.ba.ddsi.frontend.models.dtos.SolicitudDTO;
import ar.utn.ba.ddsi.frontend.models.dtos.SolicitudOutputDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Objects;

@Service
public class AdminService {
    private final WebClient webClient;

    public AdminService(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://localhost:8084/api/admin").build();
    }

    public List<SolicitudDTO> getAllSolicitudes() {
        return webClient.get().uri("/solicitudes").retrieve().bodyToFlux(SolicitudDTO.class).collectList().block();
    }

    public void actualizarSolicitud(Long id, String estado) {
        SolicitudOutputDTO dtoBack = new SolicitudOutputDTO();

        if(Objects.equals(estado, "RECHAZADA")) dtoBack.setEstado(EstadoSolicitud.RECHAZADA);
        else dtoBack.setEstado(EstadoSolicitud.ACEPTADA);

        webClient.patch()
                .uri("/solicitudes/" + id)
                .bodyValue(dtoBack)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
