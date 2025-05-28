package ar.utn.ba.ddsi.estadisticas.domain.repositorie;

import ar.utn.ba.ddsi.estadisticas.domain.request.SolicitudDeEliminacion;

public interface ISolicitudesRepository {
  void save(SolicitudDeEliminacion solicitudDeEliminacion);
}
