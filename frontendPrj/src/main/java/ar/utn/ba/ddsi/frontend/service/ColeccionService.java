package ar.utn.ba.ddsi.frontend.service;

import ar.utn.ba.ddsi.frontend.models.dtos.ColeccionDTO;
import ar.utn.ba.ddsi.frontend.models.dtos.HechoDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public List<HechoDTO> getHechosColeccionByHandle(
            String handle,
            String ubicacion,
            LocalDate fecha_hasta,
            LocalDate fecha_desde,
            String categoria,
            String modo) {

        // Convertir LocalDate a LocalDateTime (inicio y final del día)
        LocalDateTime fechaDesdeDateTime = fecha_desde != null ? fecha_desde.atStartOfDay() : null;
        LocalDateTime fechaHastaDateTime = fecha_hasta != null ? fecha_hasta.atTime(23, 59, 59) : null;

        List<HechoDTO> hechos = webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/colecciones/" + handle + "/hechos");
                    if (categoria != null && !categoria.isEmpty()) {
                        uriBuilder.queryParam("categoria", categoria);
                    }
                    if (ubicacion != null && !ubicacion.isEmpty()) {
                        uriBuilder.queryParam("ubicacion", ubicacion);
                    }
                    if (fechaDesdeDateTime != null) {
                        uriBuilder.queryParam("fecha_acontecimiento_desde", fechaDesdeDateTime.toString());
                    }
                    if (fechaHastaDateTime != null) {
                        uriBuilder.queryParam("fecha_acontecimiento_hasta", fechaHastaDateTime.toString());
                    }
                    if (modo != null && !modo.isEmpty()) {
                        uriBuilder.queryParam("modo", modo);
                    }
                    return uriBuilder.build();
                })
                .retrieve()
                .bodyToFlux(HechoDTO.class)
                .collectList()
                .block();

        return hechos != null ? hechos : List.of();
    }

    public List<String> getCategoriasUnicas(){
        // Obtener categorías directamente desde el agregador
        List<String> categorias = webClient.get()
                .uri("/front/categorias")
                .retrieve()
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<List<String>>() {})
                .block();

        return categorias != null ? categorias : List.of();
    }

    public List<String> getProvinciasConHechos(){
        // Obtener provincias con hechos desde el agregador
        List<String> provincias = webClient.get()
                .uri("/front/provincias")
                .retrieve()
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<List<String>>() {})
                .block();

        return provincias != null ? provincias : List.of();
    }
}
