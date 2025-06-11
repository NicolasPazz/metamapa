package ar.utn.ba.ddsi.agregador.service.impl;

import ar.utn.ba.ddsi.agregador.domain.dto.external.ColeccionDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.HechoDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.SolicitudDeEliminacionDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.output.SolicitudOutputDTO;
import ar.utn.ba.ddsi.agregador.infra.fuente.IFuente;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FuentesService {

  private final List<IFuente> fuentes = new ArrayList<>();

  public void agregarFuente(IFuente fuente) {
    if (!fuentes.contains(fuente)) {
      fuentes.add(fuente);
    }
  }

  public Mono<List<HechoDTO>> getAllHechosVisibles(String categoria, LocalDate fecha_reporte_desde, LocalDate fecha_reporte_hasta, LocalDate fecha_acontecimiento_desde,LocalDate fecha_acontecimiento_hasta, String ubicacion) {
    return Flux.fromIterable(fuentes)
        .flatMap(f -> f.getAllHechosVisibles(categoria, fecha_reporte_desde,
            fecha_reporte_hasta, fecha_acontecimiento_desde, fecha_acontecimiento_hasta, ubicacion))
        .flatMap(Flux::fromIterable)
        .collectList();
  }

  public Mono<List<HechoDTO>> getAllHechosNuevos() {
    return Flux.fromIterable(fuentes)
        .flatMap(IFuente::getAllHechosNuevos)
        .flatMap(Flux::fromIterable)
        .collectList();
  }

  public Mono<List<ColeccionDTO>> getAllColecciones() {
    return Flux.fromIterable(fuentes)
        .flatMap(IFuente::getAllColecciones)
        .flatMap(Flux::fromIterable)
        .collectList();
  }

  public Mono<ColeccionDTO> getColeccionByHandle(String handle) {
    return Flux.fromIterable(fuentes)
        .flatMap(f -> f.getColeccionByHandle(handle))
        .next();
  }

  public Mono<SolicitudOutputDTO> crearSolicitudDeEliminacion(SolicitudDeEliminacionDTO solicitud) {
    // TODO: Buscar que fuente tiene el hecho y redirigir la solicitud a esa fuente? o manejarla solo para esta instancia metamapa? o ambas?
    return Mono.empty();
  }
}
