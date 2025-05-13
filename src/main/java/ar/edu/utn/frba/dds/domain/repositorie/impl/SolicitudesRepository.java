package ar.edu.utn.frba.dds.domain.repositorie.impl;

import ar.edu.utn.frba.dds.domain.repositorie.ISolicitudesRepository;
import ar.edu.utn.frba.dds.domain.request.SolicitudDeEliminacion;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SolicitudesRepository implements ISolicitudesRepository {
  private final List<SolicitudDeEliminacion> solicitudes = new ArrayList<>();
  private Long nextId = 1L;

  @Override
  public void save(SolicitudDeEliminacion solicitud) {
    solicitud.setId(nextId++);
    this.solicitudes.add(solicitud);
  }
}
