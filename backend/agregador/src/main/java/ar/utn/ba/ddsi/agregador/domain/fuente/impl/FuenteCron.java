package ar.utn.ba.ddsi.agregador.domain.fuente.impl;

import ar.utn.ba.ddsi.agregador.dto.external.ColeccionDTO;
import ar.utn.ba.ddsi.agregador.dto.HechoDTO;
import ar.utn.ba.ddsi.agregador.dto.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.agregador.dto.output.SolicitudOutputDTO;
import ar.utn.ba.ddsi.agregador.domain.fuente.IFuente;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class FuenteCron implements IFuente {

  private final WebClient webClient;
  private final Long id;


  @Override
  public ResponseEntity<List<HechoDTO>> getAllHechosVisibles(
          String categoria,
          LocalDate fecha_reporte_desde, LocalDate fecha_reporte_hasta,
          LocalDate fecha_acontecimiento_desde, LocalDate fecha_acontecimiento_hasta,
          String ubicacion) {
    return ResponseEntity.ok(List.of());
  }

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
  public ResponseEntity<List<HechoDTO>> getHechosColeccionByHandle(
      UUID handle,
      String categoria,
      LocalDate fecha_reporte_desde, LocalDate fecha_reporte_hasta,
      LocalDate fecha_acontecimiento_desde, LocalDate fecha_acontecimiento_hasta,
      String ubicacion,
      String modo) {
    return ResponseEntity.of(Optional.empty());
  }

  @Override
  public ResponseEntity<SolicitudOutputDTO> crearSolicitudDeEliminacion(SolicitudInputDTO solicitud) {
    throw new UnsupportedOperationException("Not supported.");
  }
}