package ar.utn.ba.ddsi.agregador.domain.fuente.impl;

import ar.utn.ba.ddsi.agregador.domain.dto.external.ColeccionDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.HechoDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.SolicitudDeEliminacionDTO;
import ar.utn.ba.ddsi.agregador.domain.fuente.IFuente;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RequiredArgsConstructor
public class FuenteCron implements IFuente {

  private final WebClient webClient;

  @Override
  public ResponseEntity<List<HechoDTO>> getAllHechosVisibles(
      String categoria,
      LocalDate fecha_reporte_desde, LocalDate fecha_reporte_hasta,
      LocalDate fecha_acontecimiento_desde, LocalDate fecha_acontecimiento_hasta,
      String ubicacion
  ) { return ResponseEntity.ok(List.of()); }

  @Override
  public ResponseEntity<List<HechoDTO>> getAllHechosNuevos() {
    List<HechoDTO> hechos = webClient.get()
        .uri("/hechos/nuevos")
        .retrieve()
        .bodyToFlux(HechoDTO.class)
        .collectList()
        .block();
    return ResponseEntity.ok(hechos);
  }

  @Override
  public ResponseEntity<List<ColeccionDTO>> getAllColecciones() {
    return ResponseEntity.ok(List.of());
  }

  @Override
  public ResponseEntity<List<HechoDTO>> getColeccionByHandle(String handle) {
    return ResponseEntity.of(Optional.empty());
  }

  @Override
  public ResponseEntity<SolicitudDeEliminacionDTO> crearSolicitudDeEliminacion(SolicitudDeEliminacionDTO solicitud) {
    return ResponseEntity.of(Optional.empty());
  }
}