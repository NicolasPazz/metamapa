package ar.utn.ba.ddsi.servicio_agregador.domain.repositorie;

import ar.utn.ba.ddsi.servicio_agregador.domain.request.SolicitudDeEliminacion;

public interface ISolicitudesRepository {
  void save(SolicitudDeEliminacion solicitudDeEliminacion);
}
