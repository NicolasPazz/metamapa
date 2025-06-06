package ar.utn.ba.ddsi.agregador.fuente.impl;

import ar.utn.ba.ddsi.agregador.domain.dto.ColeccionDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.HechoDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.SolicitudDeEliminacionDTO;
import ar.utn.ba.ddsi.agregador.fuente.IFuente;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;

@RequiredArgsConstructor
public class FuenteCron implements IFuente {

  private final WebClient webClient;

  @Override
  public Mono<List<HechoDTO>> getAllHechosVisibles(String categoria, String fecha_reporte_desde, String fecha_reporte_hasta, String fecha_acontecimiento_desde, String fecha_acontecimiento_hasta, String ubicacion) {
    return Mono.just(List.of());
  }

  @Override
  public Mono<List<HechoDTO>> getAllHechosNuevos() {
    // lógica real para hechos nuevos
    return Mono.just(List.of());
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
  public Mono<SolicitudDeEliminacionDTO> createSolicitudDeEliminacion(SolicitudDeEliminacionDTO solicitud) {
    return Mono.empty();
  }
}