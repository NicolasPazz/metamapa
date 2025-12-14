package ar.utn.ba.ddsi.frontend.service;

import ar.utn.ba.ddsi.frontend.models.dtos.HechoDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HechoService {
    final private WebClient webClient;

    public HechoService(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://localhost:8084/api").build();
    }

    public List<HechoDTO> getAllHechos() {
        return webClient.get().uri("/hechos").retrieve().bodyToFlux(HechoDTO.class).collectList().block();
    }
    public void guardaHecho(HechoDTO hechoDTO) {

    }


    public List<HechoDTO> getAllHechosFilter(String modo, String ubicacion, LocalDate fecha_hasta, LocalDate fecha_desde, String categoria ) {
        List<HechoDTO> hechosFiltrados = webClient.get().uri("/hechos").retrieve().bodyToFlux(HechoDTO.class).collectList().block();
        assert hechosFiltrados != null;
        return hechosFiltrados.stream()
                .filter(h -> (fecha_desde == null || !h.getFechaDeAcontecimiento().isBefore(fecha_desde))
                        && (fecha_hasta == null || !h.getFechaDeAcontecimiento().isAfter(fecha_hasta)))
                .collect(Collectors.toList());
    }
    public List<String> obtenerCategoriasUnicas(){
        // 1. Traemos TODOS los hechos (sin filtrar)
        List<HechoDTO> todos = this.getAllHechos();

        // 2. Extraemos las categorías, quitamos duplicados y ordenamos
        return todos.stream()
                .map(HechoDTO::getCategoria)      // Nos quedamos solo con el String categoria
                .filter(c -> c != null && !c.isEmpty()) // Ignoramos nulos
                .distinct()                       // Borramos duplicados
                .sorted()                         // Ordenamos alfabéticamente (opcional)
                .collect(Collectors.toList());
    };

    }


