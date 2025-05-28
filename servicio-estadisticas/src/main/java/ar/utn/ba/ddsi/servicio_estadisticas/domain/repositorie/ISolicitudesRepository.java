package ar.utn.ba.ddsi.servicio_estadisticas.domain.repositorie;

import ar.utn.ba.ddsi.servicio_estadisticas.domain.request.SolicitudDeEliminacion;

public interface ISolicitudesRepository {
  void save(SolicitudDeEliminacion solicitudDeEliminacion);
}
