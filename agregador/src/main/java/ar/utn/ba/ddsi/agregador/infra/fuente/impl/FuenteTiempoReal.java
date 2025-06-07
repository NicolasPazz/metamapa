package ar.utn.ba.ddsi.agregador.infra.fuente.impl;

import ar.utn.ba.ddsi.agregador.domain.dto.external.ColeccionDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.HechoDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.SolicitudDeEliminacionDTO;
import ar.utn.ba.ddsi.agregador.infra.fuente.IFuente;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class FuenteTiempoReal implements IFuente {

  private final WebClient webClient;

  @Override
  public Mono<List<HechoDTO>> getAllHechosVisibles(String categoria, String fecha_reporte_desde, String fecha_reporte_hasta, String fecha_acontecimiento_desde, String fecha_acontecimiento_hasta, String ubicacion) {
    return webClient.get()
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
        .collectList();
  }

  @Override
  public Mono<List<HechoDTO>> getAllHechosNuevos() {
    return Mono.just(List.of());
  }

  @Override
  public Mono<List<ColeccionDTO>> getAllColecciones() {
    return webClient.get()
        .uri("/colecciones")
        .retrieve()
        .bodyToFlux(ColeccionDTO.class)
        .collectList();
  }

  @Override
  public Mono<ColeccionDTO> getColeccionByHandle(String handle) {
    return webClient.get()
        .uri("/colecciones/{handle}/hechos", handle)
        .retrieve()
        .bodyToMono(ColeccionDTO.class);
  }

  @Override
  public Mono<SolicitudDeEliminacionDTO> crearSolicitudDeEliminacion(SolicitudDeEliminacionDTO solicitud) {
    return webClient.post()
        .uri("/solicitudes")
        .bodyValue(solicitud)
        .retrieve()
        .bodyToMono(SolicitudDeEliminacionDTO.class);
  }
}
