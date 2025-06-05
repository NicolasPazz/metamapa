package ar.utn.ba.ddsi.estatica.service;

import ar.utn.ba.ddsi.estatica.domain.dto.output.HechoOutputDTO;
import java.util.List;

public interface IFuenteService {
  List<HechoOutputDTO> getAllHechosVisibles(String categoria, String fecha_reporte_desde, String fecha_reporte_hasta, String fecha_acontecimiento_desde, String fecha_acontecimiento_hasta, String ubicacion);
  List<HechoOutputDTO> getAllHechosNuevos();
}
