package ar.utn.ba.ddsi.agregador.domain.repositories.impl;

import ar.utn.ba.ddsi.agregador.domain.repositories.ISolicitudesRepository;
import ar.utn.ba.ddsi.agregador.domain.solicitud.SolicitudDeEliminacion;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SolicitudesRepository implements ISolicitudesRepository {
  private final List<SolicitudDeEliminacion> solicitudes = new ArrayList<>();

  @Override
  public void save(SolicitudDeEliminacion solicitud) {
    this.solicitudes.add(solicitud);
    solicitud.setId((long) (this.solicitudes.size()));
  }
}
