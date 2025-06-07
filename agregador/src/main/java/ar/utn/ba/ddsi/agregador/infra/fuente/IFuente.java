package ar.utn.ba.ddsi.agregador.infra.fuente;

import ar.utn.ba.ddsi.agregador.domain.dto.external.ColeccionDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.HechoDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.SolicitudDeEliminacionDTO;
import reactor.core.publisher.Mono;
import java.util.List;

public interface IFuente {

  Mono<List<HechoDTO>> getAllHechosVisibles(String categoria,
                                                  String fecha_reporte_desde,
                                                  String fecha_reporte_hasta,
                                                  String fecha_acontecimiento_desde,
                                                  String fecha_acontecimiento_hasta,
                                                  String ubicacion);

  Mono<List<HechoDTO>> getAllHechosNuevos();

  Mono<List<ColeccionDTO>> getAllColecciones();

  Mono<ColeccionDTO> getColeccionByHandle(String handle);

  Mono<SolicitudDeEliminacionDTO> crearSolicitudDeEliminacion(SolicitudDeEliminacionDTO solicitud);
}