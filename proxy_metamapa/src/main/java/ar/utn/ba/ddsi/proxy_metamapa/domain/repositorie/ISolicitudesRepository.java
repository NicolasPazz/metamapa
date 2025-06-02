package ar.utn.ba.ddsi.proxy_metamapa.domain.repositorie;

import ar.utn.ba.ddsi.proxy_metamapa.domain.request.SolicitudDeEliminacion;

public interface ISolicitudesRepository {
  void save(SolicitudDeEliminacion solicitudDeEliminacion);
}
