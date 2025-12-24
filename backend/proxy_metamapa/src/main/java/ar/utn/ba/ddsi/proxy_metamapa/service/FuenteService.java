package ar.utn.ba.ddsi.proxy_metamapa.service;

import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.ColeccionDTO;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.HechoDTO;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.PageResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@Profile("prod-metamapa-external")
public class FuenteService {
  private final WebClient webClient;

  public FuenteService(WebClient.Builder webClientBuilder, @Value("${metamapa.external.url:http://localhost:8080/api}") String apiUrl) {
    this.webClient = webClientBuilder.baseUrl(apiUrl).build();
    log.info("FuenteService inicializado con URL de MetaMapa externa: {}", apiUrl);
  }

  public ResponseEntity<List<HechoDTO>> getAllHechosVisibles(
      String categoria,
      LocalDate fecha_reporte_desde, LocalDate fecha_reporte_hasta,
      LocalDateTime fecha_acontecimiento_desde, LocalDateTime fecha_acontecimiento_hasta,
      String ubicacion
  ) {
    List<HechoDTO> hechos = webClient
        .get()
        .uri(uriBuilder -> uriBuilder
            .path("/hechos")
            .queryParamIfPresent("categoria", Optional.ofNullable(categoria))
            .queryParamIfPresent("fecha_reporte_desde", Optional.ofNullable(fecha_reporte_desde))
            .queryParamIfPresent("fecha_reporte_hasta", Optional.ofNullable(fecha_reporte_hasta))
            .queryParamIfPresent("fecha_acontecimiento_desde", Optional.ofNullable(fecha_acontecimiento_desde))
            .queryParamIfPresent("fecha_acontecimiento_hasta", Optional.ofNullable(fecha_acontecimiento_hasta))
            .queryParamIfPresent("ubicacion", Optional.ofNullable(ubicacion))
            .build()
        )
        .retrieve()
        .bodyToFlux(HechoDTO.class)
        .collectList()
        .block();

    return ResponseEntity.of(Optional.ofNullable(hechos));
  }

  public ResponseEntity<List<ColeccionDTO>> getAllColecciones() {
    List<ColeccionDTO> colecciones = webClient
        .get()
        .uri("/colecciones")
        .retrieve()
        .bodyToFlux(ColeccionDTO.class)
        .collectList()
        .block();

    return ResponseEntity.of(Optional.ofNullable(colecciones));
  }

  public ResponseEntity<List<HechoDTO>> getHechosColeccionByHandle(
      String handle,
      String categoria,
      LocalDate fecha_reporte_desde, LocalDate fecha_reporte_hasta,
      LocalDateTime fecha_acontecimiento_desde, LocalDateTime fecha_acontecimiento_hasta,
      String ubicacion
  ) {
    List<HechoDTO> hechos = webClient.get()
        .uri(uriBuilder -> uriBuilder
            .path(String.format("/colecciones/%s/hechos", handle))
            .queryParamIfPresent("categoria", Optional.ofNullable(categoria))
            .queryParamIfPresent("fecha_reporte_desde", Optional.ofNullable(fecha_reporte_desde))
            .queryParamIfPresent("fecha_reporte_hasta", Optional.ofNullable(fecha_reporte_hasta))
            .queryParamIfPresent("fecha_acontecimiento_desde", Optional.ofNullable(fecha_acontecimiento_desde))
            .queryParamIfPresent("fecha_acontecimiento_hasta", Optional.ofNullable(fecha_acontecimiento_hasta))
            .queryParamIfPresent("ubicacion", Optional.ofNullable(ubicacion))
            .build(handle))
        .retrieve()
        .bodyToFlux(HechoDTO.class)
        .collectList()
        .block();

    return ResponseEntity.of(Optional.ofNullable(hechos));
  }

  public ResponseEntity<PageResponse<HechoDTO>> getAllHechosVisiblesPaginated(
      String categoria,
      LocalDate fecha_reporte_desde, LocalDate fecha_reporte_hasta,
      LocalDateTime fecha_acontecimiento_desde, LocalDateTime fecha_acontecimiento_hasta,
      String ubicacion,
      int page,
      int size
  ) {
    try {
      Map<String, Object> response = webClient
          .get()
          .uri(uriBuilder -> uriBuilder
              .path("/hechos")
              .queryParamIfPresent("categoria", Optional.ofNullable(categoria))
              .queryParamIfPresent("fecha_reporte_desde", Optional.ofNullable(fecha_reporte_desde))
              .queryParamIfPresent("fecha_reporte_hasta", Optional.ofNullable(fecha_reporte_hasta))
              .queryParamIfPresent("fecha_acontecimiento_desde", Optional.ofNullable(fecha_acontecimiento_desde))
              .queryParamIfPresent("fecha_acontecimiento_hasta", Optional.ofNullable(fecha_acontecimiento_hasta))
              .queryParamIfPresent("ubicacion", Optional.ofNullable(ubicacion))
              .queryParam("page", page)
              .queryParam("size", size)
              .build()
          )
          .retrieve()
          .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
          .block();

      if (response == null) {
        return ResponseEntity.ok(new PageResponse<>(new ArrayList<>(), page, size, false, page > 0, 0));
      }

      @SuppressWarnings("unchecked")
      List<HechoDTO> content = (List<HechoDTO>) response.getOrDefault("content", new ArrayList<>());
      Boolean hasNext = (Boolean) response.getOrDefault("hasNext", false);
      Boolean hasPrevious = (Boolean) response.getOrDefault("hasPrevious", false);
      Number totalElements = (Number) response.getOrDefault("totalElements", 0);

      return ResponseEntity.ok(new PageResponse<>(
          content,
          page,
          size,
          hasNext != null && hasNext,
          hasPrevious != null && hasPrevious,
          totalElements != null ? totalElements.longValue() : 0
      ));
    } catch (Exception e) {
      log.error("Error obteniendo hechos paginados: {}", e.getMessage(), e);
      return ResponseEntity.ok(new PageResponse<>(new ArrayList<>(), page, size, false, page > 0, 0));
    }
  }

  public ResponseEntity<PageResponse<ColeccionDTO>> getAllColeccionesPaginated(
      int page,
      int size
  ) {
    try {
      Map<String, Object> response = webClient
          .get()
          .uri(uriBuilder -> uriBuilder
              .path("/colecciones")
              .queryParam("page", page)
              .queryParam("size", size)
              .build()
          )
          .retrieve()
          .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
          .block();

      if (response == null) {
        return ResponseEntity.ok(new PageResponse<>(new ArrayList<>(), page, size, false, page > 0, 0));
      }

      @SuppressWarnings("unchecked")
      List<ColeccionDTO> content = (List<ColeccionDTO>) response.getOrDefault("content", new ArrayList<>());
      Boolean hasNext = (Boolean) response.getOrDefault("hasNext", false);
      Boolean hasPrevious = (Boolean) response.getOrDefault("hasPrevious", false);
      Number totalElements = (Number) response.getOrDefault("totalElements", 0);

      return ResponseEntity.ok(new PageResponse<>(
          content,
          page,
          size,
          hasNext != null && hasNext,
          hasPrevious != null && hasPrevious,
          totalElements != null ? totalElements.longValue() : 0
      ));
    } catch (Exception e) {
      log.error("Error obteniendo colecciones paginadas: {}", e.getMessage(), e);
      return ResponseEntity.ok(new PageResponse<>(new ArrayList<>(), page, size, false, page > 0, 0));
    }
  }

}