package ar.utn.ba.ddsi.dinamica.service;

import ar.utn.ba.ddsi.dinamica.domain.dto.output.HechoOutputDTO;
import ar.utn.ba.ddsi.dinamica.domain.solicitud.SolicitudHecho;

import java.time.LocalDate;
import java.util.List;

public interface IFuenteService {
  List<HechoOutputDTO> getAllHechosVisibles(String categoria, LocalDate fecha_reporte_desde, LocalDate fecha_reporte_hasta, LocalDate fecha_acontecimiento_desde, LocalDate fecha_acontecimiento_hasta, String ubicacion);
  List<HechoOutputDTO> getAllHechosNuevos();
  void crearHechoDesdeSolicitud(SolicitudHecho solicitud);
}
