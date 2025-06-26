package ar.utn.ba.ddsi.agregador.domain.fuente.impl;

import ar.utn.ba.ddsi.agregador.domain.dto.external.ColeccionDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.HechoDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.SolicitudDeEliminacionDTO;
import ar.utn.ba.ddsi.agregador.domain.fuente.IFuente;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.ResponseEntity;

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
  public ResponseEntity<List<HechoDTO>> getColeccionByHandle(String handle) {
    List<HechoDTO> hechos = webClient.get()
        .uri("/colecciones/{handle}/hechos", handle)
        .retrieve()
        .bodyToFlux(HechoDTO.class)
        .collectList()
        .block();
    return ResponseEntity.ok(hechos);
  }

  @Override
  public ResponseEntity<SolicitudDeEliminacionDTO> crearSolicitudDeEliminacion(SolicitudDeEliminacionDTO solicitud) {
    SolicitudDeEliminacionDTO resultado = webClient.post()
        .uri("/solicitudes")
        .bodyValue(solicitud)
        .retrieve()
        .bodyToMono(SolicitudDeEliminacionDTO.class)
        .block();
    return ResponseEntity.of(Optional.ofNullable(resultado));
  }
}
