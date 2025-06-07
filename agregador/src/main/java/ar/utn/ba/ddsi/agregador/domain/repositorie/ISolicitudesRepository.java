package ar.utn.ba.ddsi.agregador.domain.repositorie;

import ar.utn.ba.ddsi.agregador.domain.solicitud.SolicitudDeEliminacion;

public interface ISolicitudesRepository {
  void save(SolicitudDeEliminacion solicitudDeEliminacion);
}
