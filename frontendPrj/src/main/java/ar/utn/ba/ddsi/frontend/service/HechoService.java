package ar.utn.ba.ddsi.frontend.service;

import ar.utn.ba.ddsi.frontend.models.dtos.HechoDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
        // Convertir LocalDate a LocalDateTime (inicio y final del día)
        LocalDateTime fechaDesdeDateTime = fecha_desde != null ? fecha_desde.atStartOfDay() : null;
        LocalDateTime fechaHastaDateTime = fecha_hasta != null ? fecha_hasta.atTime(23, 59, 59) : null;

        // Construir la URI con los parámetros de filtro
        List<HechoDTO> hechos = webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/hechos");
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
                    return uriBuilder.build();
                })
                .retrieve()
                .bodyToFlux(HechoDTO.class)
                .collectList()
                .block();

        return hechos != null ? hechos : List.of();
    }

    public List<String> obtenerCategoriasUnicas(){
        // Obtener categorías directamente desde el agregador
        List<String> categorias = webClient.get()
                .uri("/front/categorias")
                .retrieve()
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<List<String>>() {})
                .block();

        return categorias != null ? categorias : List.of();
    }

    public List<String> obtenerProvinciasConHechos(){
        // Obtener provincias con hechos desde el agregador
        List<String> provincias = webClient.get()
                .uri("/front/provincias")
                .retrieve()
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<List<String>>() {})
                .block();

        return provincias != null ? provincias : List.of();
    }

    public HechoDTO getOneHecho() {
        // Obtener un solo hecho para la landing usando el endpoint optimizado
        try {
            return webClient.get()
                    .uri("/front/hecho-reciente")
                    .retrieve()
                    .bodyToMono(HechoDTO.class)
                    .block();
        } catch (Exception e) {
            // Si falla el endpoint optimizado, no devolver nada (el landing mostrará el mensaje de "no hay hechos")
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public ar.utn.ba.ddsi.frontend.models.PageResponse<HechoDTO> getAllHechosPaginados(String modo, String ubicacion, LocalDate fecha_hasta, LocalDate fecha_desde, String categoria, int page, int size) {
        LocalDateTime fechaDesdeDateTime = fecha_desde != null ? fecha_desde.atStartOfDay() : null;
        LocalDateTime fechaHastaDateTime = fecha_hasta != null ? fecha_hasta.atTime(23, 59, 59) : null;

        try {
            Map<String, Object> response = webClient.get()
                    .uri(uriBuilder -> {
                        uriBuilder.path("/front/hechos");
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
                        uriBuilder.queryParam("page", page);
                        uriBuilder.queryParam("size", size);
                        return uriBuilder.build();
                    })
                    .retrieve()
                    .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            // Procesar respuesta y extraer datos
            if (response != null) {
                List<HechoDTO> content = (List<HechoDTO>) response.getOrDefault("content", List.of());
                Boolean hasNext = (Boolean) response.getOrDefault("hasNext", false);
                Boolean hasPrevious = (Boolean) response.getOrDefault("hasPrevious", false);
                Integer currentPage = (Integer) response.getOrDefault("currentPage", page);
                Integer pageSize = (Integer) response.getOrDefault("pageSize", size);
                Number totalElements = (Number) response.getOrDefault("totalElements", 0);

                return new ar.utn.ba.ddsi.frontend.models.PageResponse<>(
                    content,
                    hasNext,
                    hasPrevious,
                    currentPage,
                    pageSize,
                    totalElements.longValue()
                );
            }

            return new ar.utn.ba.ddsi.frontend.models.PageResponse<>(List.of(), false, false, page, size, 0L);
        } catch (Exception e) {
            return new ar.utn.ba.ddsi.frontend.models.PageResponse<>(List.of(), false, false, page, size, 0L);
        }
    }
}
