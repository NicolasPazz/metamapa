package ar.utn.ba.ddsi.agregador.domain.fuente.impl;

import ar.utn.ba.ddsi.agregador.dto.ColeccionDTO;
import ar.utn.ba.ddsi.agregador.dto.HechoDTO;
import ar.utn.ba.ddsi.agregador.domain.fuente.AFuente;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@NoArgsConstructor
@Entity
@DiscriminatorValue("TIEMPO_REAL")
public class FuenteTiempoReal extends AFuente {

  @Transient
  @Getter
  @Setter
  private WebClient webClient;

  public FuenteTiempoReal(Long id, String nombre, WebClient webClient) {
    super(id, nombre);
    this.webClient = webClient;
  }

  @Transient
  @Override
  public ResponseEntity<List<HechoDTO>> getAllHechosVisibles(
      String categoria,
      LocalDate fecha_reporte_desde, LocalDate fecha_reporte_hasta,
      LocalDateTime fecha_acontecimiento_desde, LocalDateTime fecha_acontecimiento_hasta,
      String ubicacion) {
    List<HechoDTO> hechos = webClient.get()
        .uri(uriBuilder -> uriBuilder
            .path("/hechos")
            .queryParamIfPresent("categoria", Optional.ofNullable(categoria))
            .queryParamIfPresent("fecha_reporte_desde", Optional.ofNullable(fecha_reporte_desde))
            .queryParamIfPresent("fecha_reporte_hasta", Optional.ofNullable(fecha_reporte_hasta))
            .queryParamIfPresent("fecha_acontecimiento_desde", Optional.ofNullable(fecha_acontecimiento_desde))
            .queryParamIfPresent("fecha_acontecimiento_hasta", Optional.ofNullable(fecha_acontecimiento_hasta))
            .queryParamIfPresent("ubicacion", Optional.ofNullable(ubicacion))
            .build())
        .retrieve()
        .bodyToFlux(HechoDTO.class)
        .collectList()
        .block();
    return ResponseEntity.of(Optional.ofNullable(hechos));
  }

  @Transient
  @Override
  public ResponseEntity<List<HechoDTO>> getAllHechosNuevos() {
    return ResponseEntity.ok(List.of());
  }

  @Transient
  @Override
  public ResponseEntity<List<ColeccionDTO>> getAllColecciones() {
    try {
      log.info("Obteniendo colecciones de fuente tiempo real: {}", getNombre());
      List<ColeccionDTO> colecciones = getWebClient().get()
          .uri("/colecciones")
          .retrieve()
          .bodyToFlux(ColeccionDTO.class)
          .collectList()
          .block();
      log.info("Colecciones obtenidas: {}", colecciones != null ? colecciones.size() : 0);
      return ResponseEntity.of(Optional.ofNullable(colecciones));
    } catch (Exception e) {
      log.error("Error obteniendo colecciones de fuente {}: {}", getNombre(), e.getMessage(), e);
      return ResponseEntity.ok(List.of());
    }
  }

  @Transient
  @Override
  public ResponseEntity<List<HechoDTO>> getHechosColeccionByHandle(
      UUID handle,
      String categoria,
      LocalDate fecha_reporte_desde, LocalDate fecha_reporte_hasta,
      LocalDateTime fecha_acontecimiento_desde, LocalDateTime fecha_acontecimiento_hasta,
      String ubicacion,
      String modo) {
    List<HechoDTO> hechos = webClient.get()
        .uri(uriBuilder -> uriBuilder
            .path(String.format("/colecciones/%s/hechos", handle))
            .queryParamIfPresent("categoria", Optional.ofNullable(categoria))
            .queryParamIfPresent("fecha_reporte_desde", Optional.ofNullable(fecha_reporte_desde))
            .queryParamIfPresent("fecha_reporte_hasta", Optional.ofNullable(fecha_reporte_hasta))
            .queryParamIfPresent("fecha_acontecimiento_desde", Optional.ofNullable(fecha_acontecimiento_desde))
            .queryParamIfPresent("fecha_acontecimiento_hasta", Optional.ofNullable(fecha_acontecimiento_hasta))
            .queryParamIfPresent("ubicacion", Optional.ofNullable(ubicacion))
            .queryParamIfPresent("modo", Optional.ofNullable(modo))
            .build(handle))
        .retrieve()
        .bodyToFlux(HechoDTO.class)
        .collectList()
        .block();

    return ResponseEntity.ok(hechos);
  }
}
