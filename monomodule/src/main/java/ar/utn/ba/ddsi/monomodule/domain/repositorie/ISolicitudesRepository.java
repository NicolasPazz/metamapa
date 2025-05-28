package ar.utn.ba.ddsi.monomodule.domain.repositorie;

import ar.utn.ba.ddsi.monomodule.domain.request.SolicitudDeEliminacion;

public interface ISolicitudesRepository {
  void save(SolicitudDeEliminacion solicitudDeEliminacion);
}
