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




    public List<HechoDTO> getAllHechosFilter(String modo, String ubicacion, LocalDate fecha_desde, LocalDate fecha_hasta, String categoria ) {
        List<HechoDTO>hechosFiltrados =webClient.get().uri("/hechos").retrieve().bodyToFlux(HechoDTO.class).collectList().block();
        assert hechosFiltrados != null;
        return hechosFiltrados.stream()
                // FILTRO DE UBICACIÓN
                // Lógica: Si 'ubicacion' es nulo O vacío, pasa true (ignora filtro). Si no, compara ignorando mayúsculas.
                //.filter(h -> ubicacion == null || ubicacion.isEmpty() || h.getUbicacion().equalsIgnoreCase(ubicacion))

                .filter(h-> categoria == null || categoria.isEmpty() || h.getCategoria().equalsIgnoreCase(categoria))

                // FILTRO DE FECHA (Asumo que filtras por fecha de acontecimiento)
                .filter(h -> (fecha_desde == null || !h.getFechaDeAcontecimiento().isBefore(fecha_desde))
                        && (fecha_hasta == null || !h.getFechaDeAcontecimiento().isAfter(fecha_hasta)))
                .collect(Collectors.toList());
    }

    }

