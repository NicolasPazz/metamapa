package ar.utn.ba.ddsi.servicio_fuente_estatica.domain.repositorie;

import ar.utn.ba.ddsi.servicio_fuente_estatica.domain.request.SolicitudDeEliminacion;

public interface ISolicitudesRepository {
  void save(SolicitudDeEliminacion solicitudDeEliminacion);
}
