package ar.utn.ba.ddsi.proxy_metamapa.domain.repositorie.impl;

import ar.utn.ba.ddsi.proxy_metamapa.domain.repositorie.ISolicitudesRepository;
import ar.utn.ba.ddsi.proxy_metamapa.domain.request.SolicitudDeEliminacion;
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
