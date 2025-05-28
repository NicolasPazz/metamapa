package ar.utn.ba.ddsi.fuenteestatica.domain.repositorie;

import ar.utn.ba.ddsi.fuenteestatica.domain.request.SolicitudDeEliminacion;

public interface ISolicitudesRepository {
  void save(SolicitudDeEliminacion solicitudDeEliminacion);
}
