package ar.utn.ba.ddsi.agregador.infra.fuente;

import ar.utn.ba.ddsi.agregador.domain.dto.external.ColeccionDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.HechoDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.SolicitudDeEliminacionDTO;
import java.time.LocalDate;
import reactor.core.publisher.Mono;
import java.util.List;

public interface IFuente {

  Mono<List<HechoDTO>> getAllHechosVisibles(String categoria,
                                            LocalDate fecha_reporte_desde,
                                            LocalDate fecha_reporte_hasta,
                                            LocalDate fecha_acontecimiento_desde,
                                            LocalDate fecha_acontecimiento_hasta,
                                            String ubicacion);

  Mono<List<HechoDTO>> getAllHechosNuevos();

  Mono<List<ColeccionDTO>> getAllColecciones();

  Mono<ColeccionDTO> getColeccionByHandle(String handle);

  Mono<SolicitudDeEliminacionDTO> crearSolicitudDeEliminacion(SolicitudDeEliminacionDTO solicitud);
}