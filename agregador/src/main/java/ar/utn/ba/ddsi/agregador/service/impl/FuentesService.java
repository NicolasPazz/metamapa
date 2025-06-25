package ar.utn.ba.ddsi.agregador.service.impl;

import ar.utn.ba.ddsi.agregador.domain.dto.external.ColeccionDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.HechoDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.SolicitudDeEliminacionDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.output.SolicitudOutputDTO;
import ar.utn.ba.ddsi.agregador.domain.fuente.IFuente;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;

@Slf4j
@Service
public class FuentesService {

  private final List<IFuente> fuentes = new ArrayList<>();

  public void agregarFuente(IFuente fuente) {
    if (!fuentes.contains(fuente)) {
      fuentes.add(fuente);
    }
  }

  public ResponseEntity<List<HechoDTO>> getAllHechosVisibles(
      String categoria,
      LocalDate fecha_reporte_desde, LocalDate fecha_reporte_hasta,
      LocalDate fecha_acontecimiento_desde,LocalDate fecha_acontecimiento_hasta,
      String ubicacion) {
    List<HechoDTO> resultados = new ArrayList<>();
    for (IFuente fuente : fuentes) {
      List<HechoDTO> hechos = Optional.ofNullable(
          fuente.getAllHechosVisibles(categoria,
              fecha_reporte_desde, fecha_reporte_hasta,
              fecha_acontecimiento_desde, fecha_acontecimiento_hasta,
              ubicacion).getBody()
      ).orElse(List.of());
      resultados.addAll(hechos);
    }

    log.info("[FuentesService] Se obtuvieron {} hechos visibles combinados de todas las fuentes.", resultados.size());

    return ResponseEntity.of(Optional.of(resultados));
  }

  public ResponseEntity<List<HechoDTO>> getAllHechosNuevos() {
    List<HechoDTO> resultados = new ArrayList<>();
    for (IFuente fuente : fuentes) {
      List<HechoDTO> hechos = Optional.ofNullable(
          fuente.getAllHechosNuevos().getBody()
      ).orElse(List.of());
      resultados.addAll(hechos);
    }
    return ResponseEntity.of(Optional.of(resultados));
  }

  public ResponseEntity<List<ColeccionDTO>> getAllColecciones() {
    List<ColeccionDTO> resultados = new ArrayList<>();
    for (IFuente fuente : fuentes) {
      List<ColeccionDTO> colecciones = Optional.ofNullable(
          fuente.getAllColecciones().getBody()
      ).orElse(List.of());
      resultados.addAll(colecciones);
    }
    return ResponseEntity.of(Optional.of(resultados));
  }

  public ResponseEntity<List<HechoDTO>> getColeccionByHandle(String handle) {
    for (IFuente fuente : fuentes) {
      ResponseEntity<List<HechoDTO>> respuesta = fuente.getColeccionByHandle(handle);
      if (respuesta.getBody() != null) {
        return ResponseEntity.of(Optional.of(respuesta.getBody()));
      }
    }
    return ResponseEntity.of(Optional.empty());
  }

  public ResponseEntity<SolicitudOutputDTO> crearSolicitudDeEliminacion(SolicitudDeEliminacionDTO solicitud) {
    // TODO: Buscar que fuente tiene el hecho y redirigir la solicitud a esa fuente? o manejarla solo para esta instancia metamapa? o ambas?
    return ResponseEntity.of(Optional.empty());
  }
}
