package ar.utn.frba.dds.metamapa.domain.repositorie;

import ar.utn.frba.dds.metamapa.domain.request.SolicitudDeEliminacion;

public interface ISolicitudesRepository {
  void save(SolicitudDeEliminacion solicitudDeEliminacion);
}
