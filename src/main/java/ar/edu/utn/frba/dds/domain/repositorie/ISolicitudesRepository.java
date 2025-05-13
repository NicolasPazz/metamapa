package ar.edu.utn.frba.dds.domain.repositorie;

import ar.edu.utn.frba.dds.domain.request.SolicitudDeEliminacion;
import java.util.List;

public interface ISolicitudesRepository {
  void save(SolicitudDeEliminacion solicitudDeEliminacion);
}
