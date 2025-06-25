package ar.utn.ba.ddsi.agregador.domain.fuente;

import ar.utn.ba.ddsi.agregador.domain.dto.external.ColeccionDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.HechoDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.SolicitudDeEliminacionDTO;
import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import java.util.List;

public interface IFuente {

  ResponseEntity<List<HechoDTO>> getAllHechosVisibles(String categoria,
                                                      LocalDate fecha_reporte_desde,
                                                      LocalDate fecha_reporte_hasta,
                                                      LocalDate fecha_acontecimiento_desde,
                                                      LocalDate fecha_acontecimiento_hasta,
                                                      String ubicacion);

  ResponseEntity<List<HechoDTO>> getAllHechosNuevos();

  ResponseEntity<List<ColeccionDTO>> getAllColecciones();

  ResponseEntity<List<HechoDTO>> getColeccionByHandle(String handle);

  ResponseEntity<SolicitudDeEliminacionDTO> crearSolicitudDeEliminacion(SolicitudDeEliminacionDTO solicitud);
}