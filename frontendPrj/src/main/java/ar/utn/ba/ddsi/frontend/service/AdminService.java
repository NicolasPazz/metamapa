package ar.utn.ba.ddsi.frontend.service;

import ar.utn.ba.ddsi.frontend.models.dtos.*;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Objects;

@Service
public class AdminService {
    private final WebClient webClientAgregador;
    private final WebClient webClientEstatica;

    public AdminService(WebClient.Builder builder) {
        this.webClientAgregador = builder.baseUrl("http://localhost:8084/api/admin").build();
        this.webClientEstatica = builder.baseUrl("http://localhost:8082/api/hechos").build(); //Se tiene que establecer una conexion con la estatica
        // porque la carga de CSV tiene que ir directamente a la estatica
    }

    public List<SolicitudDTO> getAllSolicitudes() {
        return webClientAgregador.get().uri("/solicitudes").retrieve().bodyToFlux(SolicitudDTO.class).collectList().block();
    }

    public void actualizarSolicitud(Long id, String estado) {
        SolicitudOutputDTO dtoBack = new SolicitudOutputDTO();

        if(Objects.equals(estado, "RECHAZADA")) dtoBack.setEstado(EstadoSolicitud.RECHAZADA);
        else dtoBack.setEstado(EstadoSolicitud.ACEPTADA);

        webClientAgregador.patch()
                .uri("/solicitudes/" + id)
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
                .uri("/carga-masiva") //
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public void crearColeccion(CrearColeccionDTO dto) {
        webClientAgregador
                .post()
                .uri("/colecciones")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(Void.class) // Deserialize the response
                .block();
    }

    public void actualizarColeccion(CrearColeccionDTO dto, String handle) {
        webClientAgregador.patch()
                .uri("/colecciones/" + handle)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
