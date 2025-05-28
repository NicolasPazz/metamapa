package ar.utn.ba.ddsi.servicio_fuente_proxy.domain.repositorie;

import ar.utn.ba.ddsi.servicio_fuente_proxy.domain.request.SolicitudDeEliminacion;

public interface ISolicitudesRepository {
  void save(SolicitudDeEliminacion solicitudDeEliminacion);
}
