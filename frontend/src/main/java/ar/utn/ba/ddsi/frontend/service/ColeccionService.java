package ar.utn.ba.ddsi.frontend.service;

import ar.utn.ba.ddsi.frontend.models.PageResponse;
import ar.utn.ba.ddsi.frontend.models.dtos.ColeccionDTO;
import ar.utn.ba.ddsi.frontend.models.dtos.HechoDTO;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ColeccionService {
    private final WebClient webClient;

    public ColeccionService(WebClient.Builder builder,
                           @Value("${app.api.agregador.base-url:http://localhost:8084/api}") String agregadorBaseUrl) {
        this.webClient = builder.baseUrl(agregadorBaseUrl).build();
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
        List<String> categorias = webClient.get()
                .uri("/front/categorias")
                .retrieve()
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<List<String>>() {})
                .block();

        return categorias != null ? categorias : List.of();
    }

    public List<String> getFuentesConHechos(){
        List<String> fuentes = webClient.get()
                .uri("/front/fuentes")
                .retrieve()
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<List<String>>() {})
                .block();

        return fuentes != null ? fuentes : List.of();
    }

    public List<String> getProvinciasConHechos(){
        List<String> provincias = webClient.get()
                .uri("/front/provincias")
                .retrieve()
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<List<String>>() {})
                .block();

        return provincias != null ? provincias : List.of();
    }

    public ColeccionDTO getOneColeccion() {
        try {
            return webClient.get()
                    .uri("/front/coleccion-reciente")
                    .retrieve()
                    .bodyToMono(ColeccionDTO.class)
                    .block();
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public PageResponse<ColeccionDTO> getAllColeccionesPaginadas(int page, int size) {
        try {
            Map<String, Object> response = webClient.get()
                    .uri(uriBuilder -> {
                        uriBuilder.path("/front/colecciones");
                        uriBuilder.queryParam("page", page);
                        uriBuilder.queryParam("size", size);
                        return uriBuilder.build();
                    })
                    .retrieve()
                    .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            if (response != null) {
                List<ColeccionDTO> content = (List<ColeccionDTO>) response.getOrDefault("content", List.of());
                Boolean hasNext = (Boolean) response.getOrDefault("hasNext", false);
                Boolean hasPrevious = (Boolean) response.getOrDefault("hasPrevious", false);
                Integer currentPage = (Integer) response.getOrDefault("currentPage", page);
                Integer pageSize = (Integer) response.getOrDefault("pageSize", size);
                Number totalElements = (Number) response.getOrDefault("totalElements", 0);

                return new PageResponse<>(
                    content,
                    hasNext,
                    hasPrevious,
                    currentPage,
                    pageSize,
                    totalElements.longValue()
                );
            }

            return new PageResponse<>(List.of(), false, false, page, size, 0L);
        } catch (Exception e) {
            return new PageResponse<>(List.of(), false, false, page, size, 0L);
        }
    }

    @SuppressWarnings("unchecked")
    public PageResponse<HechoDTO> getHechosColeccionPaginados(
            String handle,
            String ubicacion,
            LocalDate fecha_hasta,
            LocalDate fecha_desde,
            String categoria,
            String fuente,
            String modo,
            int page,
            int size) {

        try {
            Map<String, Object> response = webClient.get()
                    .uri(uriBuilder -> {
                        uriBuilder.path("/front/colecciones/" + handle + "/hechos");
                        if (categoria != null && !categoria.isEmpty()) {
                            uriBuilder.queryParam("categoria", categoria);
                        }
                        if (ubicacion != null && !ubicacion.isEmpty()) {
                            uriBuilder.queryParam("ubicacion", ubicacion);
                        }
                        if (fuente != null && !fuente.isEmpty()) {
                            uriBuilder.queryParam("fuente", fuente);
                        }
                        if (fecha_desde != null) {
                            uriBuilder.queryParam("fecha_reporte_desde", fecha_desde.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                        }
                        if (fecha_hasta != null) {
                            uriBuilder.queryParam("fecha_reporte_hasta", fecha_hasta.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                        }
                        if (modo != null && !modo.isEmpty()) {
                            uriBuilder.queryParam("modo", modo);
                        }
                        uriBuilder.queryParam("page", page);
                        uriBuilder.queryParam("size", size);
                        return uriBuilder.build();
                    })
                    .retrieve()
                    .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            if (response != null) {
                List<HechoDTO> content = (List<HechoDTO>) response.getOrDefault("content", List.of());
                Boolean hasNext = (Boolean) response.getOrDefault("hasNext", false);
                Boolean hasPrevious = (Boolean) response.getOrDefault("hasPrevious", false);
                Integer currentPage = (Integer) response.getOrDefault("currentPage", page);
                Integer pageSize = (Integer) response.getOrDefault("pageSize", size);
                Number totalElements = (Number) response.getOrDefault("totalElements", 0);

                return new PageResponse<>(
                    content,
                    hasNext,
                    hasPrevious,
                    currentPage,
                    pageSize,
                    totalElements.longValue()
                );
            }

            return new PageResponse<>(List.of(), false, false, page, size, 0L);
        } catch (Exception e) {
            return new PageResponse<>(List.of(), false, false, page, size, 0L);
        }
    }
}
