package ar.utn.ba.ddsi.agregador.domain.fuente;

import ar.utn.ba.ddsi.agregador.domain.dto.external.ColeccionDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.HechoDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.output.SolicitudOutputDTO;
import ar.utn.ba.ddsi.agregador.domain.coleccion.ModoNavegacion;
import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import java.util.List;

public interface IFuente {

  Long getId();

  ResponseEntity<List<HechoDTO>> getAllHechosVisibles(String categoria,
                                                      LocalDate fecha_reporte_desde,
                                                      LocalDate fecha_reporte_hasta,
                                                      LocalDate fecha_acontecimiento_desde,
                                                      LocalDate fecha_acontecimiento_hasta,
                                                      String ubicacion,
                                                      ModoNavegacion modo);

  ResponseEntity<List<HechoDTO>> getAllHechosNuevos();

  ResponseEntity<List<ColeccionDTO>> getAllColecciones();

  ResponseEntity<List<HechoDTO>> getHechosColeccionByHandle(
      String handle,
      String categoria,
      LocalDate fecha_reporte_desde, LocalDate fecha_reporte_hasta,
      LocalDate fecha_acontecimiento_desde, LocalDate fecha_acontecimiento_hasta,
      String ubicacion,
      ModoNavegacion modo);

  ResponseEntity<SolicitudOutputDTO> crearSolicitudDeEliminacion(SolicitudInputDTO solicitud);
}