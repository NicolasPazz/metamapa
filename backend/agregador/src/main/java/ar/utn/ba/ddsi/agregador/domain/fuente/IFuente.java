package ar.utn.ba.ddsi.agregador.domain.fuente;

import ar.utn.ba.ddsi.agregador.dto.external.ColeccionDTO;
import ar.utn.ba.ddsi.agregador.dto.HechoDTO;
import ar.utn.ba.ddsi.agregador.dto.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.agregador.dto.output.SolicitudOutputDTO;
import java.time.LocalDate;

import java.util.UUID;
import org.springframework.http.ResponseEntity;
import java.util.List;

public interface IFuente {

  Long getId();

  ResponseEntity<List<HechoDTO>> getAllHechosVisibles(String categoria,
                                                      LocalDate fecha_reporte_desde,
                                                      LocalDate fecha_reporte_hasta,
                                                      LocalDate fecha_acontecimiento_desde,
                                                      LocalDate fecha_acontecimiento_hasta,
                                                      String ubicacion);

  ResponseEntity<List<HechoDTO>> getAllHechosNuevos();

  ResponseEntity<List<ColeccionDTO>> getAllColecciones();

  ResponseEntity<List<HechoDTO>> getHechosColeccionByHandle(
      UUID handle,
      String categoria,
      LocalDate fecha_reporte_desde, LocalDate fecha_reporte_hasta,
      LocalDate fecha_acontecimiento_desde, LocalDate fecha_acontecimiento_hasta,
      String ubicacion,
      String modo);

  ResponseEntity<SolicitudOutputDTO> crearSolicitudDeEliminacion(SolicitudInputDTO solicitud);
}