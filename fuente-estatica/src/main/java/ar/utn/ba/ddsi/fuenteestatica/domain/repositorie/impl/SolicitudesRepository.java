package ar.utn.ba.ddsi.fuenteestatica.domain.repositorie.impl;

import ar.utn.ba.ddsi.fuenteestatica.domain.repositorie.ISolicitudesRepository;
import ar.utn.ba.ddsi.fuenteestatica.domain.request.SolicitudDeEliminacion;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

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
