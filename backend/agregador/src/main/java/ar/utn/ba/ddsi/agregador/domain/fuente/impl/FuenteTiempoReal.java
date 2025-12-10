package ar.utn.ba.ddsi.agregador.domain.fuente.impl;

import ar.utn.ba.ddsi.agregador.domain.dto.external.ColeccionDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.HechoDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.output.SolicitudOutputDTO;
import ar.utn.ba.ddsi.agregador.domain.fuente.IFuente;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.ResponseEntity;

@Slf4j
@RequiredArgsConstructor
@Getter
public class FuenteTiempoReal implements IFuente {

  private final WebClient webClient;
  private final Long id;

  @Override
  public ResponseEntity<List<HechoDTO>> getAllHechosVisibles(
      String categoria,
      LocalDate fecha_reporte_desde, LocalDate fecha_reporte_hasta,
      LocalDate fecha_acontecimiento_desde, LocalDate fecha_acontecimiento_hasta,
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

  @Override
  public ResponseEntity<List<HechoDTO>> getAllHechosNuevos() {
    return ResponseEntity.ok(List.of());
  }

  @Override
  public ResponseEntity<List<ColeccionDTO>> getAllColecciones() {
    List<ColeccionDTO> colecciones = webClient.get()
        .uri("/colecciones")
        .retrieve()
        .bodyToFlux(ColeccionDTO.class)
        .collectList()
        .block();
    return ResponseEntity.of(Optional.ofNullable(colecciones));
  }

  @Override
  public ResponseEntity<List<HechoDTO>> getHechosColeccionByHandle(
      UUID handle,
      String categoria,
      LocalDate fecha_reporte_desde, LocalDate fecha_reporte_hasta,
      LocalDate fecha_acontecimiento_desde, LocalDate fecha_acontecimiento_hasta,
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

  @Override
  public ResponseEntity<SolicitudOutputDTO> crearSolicitudDeEliminacion(SolicitudInputDTO solicitud) {
    log.info("[FuenteTiempoReal] Creando solicitud de eliminacion: {}", solicitud);
    SolicitudOutputDTO resultado = webClient.post()
        .uri("/solicitudes")
        .bodyValue(solicitud)
        .retrieve()
        .bodyToMono(SolicitudOutputDTO.class)
        .block();
    return ResponseEntity.of(Optional.ofNullable(resultado));
  }
}
