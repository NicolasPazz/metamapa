package ar.utn.ba.ddsi.fuentedinamica.domain.repositorie;

import ar.utn.ba.ddsi.fuentedinamica.domain.request.SolicitudDeEliminacion;

public interface ISolicitudesRepository {
  void save(SolicitudDeEliminacion solicitudDeEliminacion);
}
