package ar.utn.ba.ddsi.fuenteproxy.domain.repositorie;

import ar.utn.ba.ddsi.fuenteproxy.domain.request.SolicitudDeEliminacion;

public interface ISolicitudesRepository {
  void save(SolicitudDeEliminacion solicitudDeEliminacion);
}
