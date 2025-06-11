package ar.utn.ba.ddsi.agregador.infra.fuente.impl;

import ar.utn.ba.ddsi.agregador.domain.dto.external.ColeccionDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.HechoDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.SolicitudDeEliminacionDTO;
import ar.utn.ba.ddsi.agregador.infra.fuente.IFuente;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;

@RequiredArgsConstructor
public class FuenteCron implements IFuente {

  private final WebClient webClient;

  @Override
  public Mono<List<HechoDTO>> getAllHechosVisibles(String categoria, LocalDate fecha_reporte_desde, LocalDate fecha_reporte_hasta, LocalDate fecha_acontecimiento_desde, LocalDate fecha_acontecimiento_hasta, String ubicacion) {
    return Mono.just(List.of());
  }

  @Override
  public Mono<List<HechoDTO>> getAllHechosNuevos() {
    return webClient.get()
        .uri("/hechos/nuevos")
        .retrieve()
        .bodyToFlux(HechoDTO.class)
        .collectList();
  }

  @Override
  public Mono<List<ColeccionDTO>> getAllColecciones() {
    return Mono.just(List.of());
  }

  @Override
  public Mono<ColeccionDTO> getColeccionByHandle(String handle) {
    return Mono.empty();
  }

  @Override
  public Mono<SolicitudDeEliminacionDTO> crearSolicitudDeEliminacion(SolicitudDeEliminacionDTO solicitud) {
    return Mono.empty();
  }
}