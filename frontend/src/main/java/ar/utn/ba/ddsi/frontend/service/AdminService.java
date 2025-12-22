package ar.utn.ba.ddsi.frontend.service;

import ar.utn.ba.ddsi.frontend.models.dtos.*;
import org.springframework.beans.factory.annotation.Value;
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

    public AdminService(WebClient.Builder builder,
                       @Value("${app.api.agregador.base-url:http://localhost:8084/api}") String agregadorBaseUrl,
                       @Value("${app.api.estatica.base-url:http://localhost:8082/api}") String estaticaBaseUrl) {
        this.webClientAgregador = builder.baseUrl(agregadorBaseUrl + "/admin").build();
        this.webClientEstatica = builder.baseUrl(estaticaBaseUrl).build();
    }

    public List<SolicitudDTO> getAllSolicitudes() {
        return webClientAgregador.get().uri("/solicitudes").retrieve().bodyToFlux(SolicitudDTO.class).collectList().block();
    }

    public void actualizarSolicitud(Long id, String estado) {
        SolicitudOutputDTO dtoBack = new SolicitudOutputDTO();

        if(Objects.equals(estado, "RECHAZADO")) dtoBack.setEstado(EstadoSolicitud.RECHAZADO);
        else dtoBack.setEstado(EstadoSolicitud.ACEPTADO);

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
                .uri("/csv")
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
                .bodyToMono(Void.class)
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

    public void agregarEtiquetasAHecho(Long hechoId, List<String> etiquetas) {
        if (hechoId == null) {
            throw new IllegalArgumentException("El ID del hecho no puede ser nulo");
        }
        if (etiquetas == null || etiquetas.isEmpty()) {
            throw new IllegalArgumentException("Debe proporcionar al menos una etiqueta");
        }

        webClientAgregador.patch()
                .uri("/hechos/"+ hechoId + "/etiquetas")
                .bodyValue(etiquetas)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
