package ar.utn.ba.ddsi.agregador.domain.repositories;

import ar.utn.ba.ddsi.agregador.domain.solicitud.SolicitudDeEliminacion;

public interface ISolicitudesRepository {
  void save(SolicitudDeEliminacion solicitudDeEliminacion);
}
