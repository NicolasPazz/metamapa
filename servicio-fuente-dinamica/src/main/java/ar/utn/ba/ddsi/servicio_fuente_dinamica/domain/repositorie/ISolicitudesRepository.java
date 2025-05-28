package ar.utn.ba.ddsi.servicio_fuente_dinamica.domain.repositorie;

import ar.utn.ba.ddsi.servicio_fuente_dinamica.domain.request.SolicitudDeEliminacion;

public interface ISolicitudesRepository {
  void save(SolicitudDeEliminacion solicitudDeEliminacion);
}
