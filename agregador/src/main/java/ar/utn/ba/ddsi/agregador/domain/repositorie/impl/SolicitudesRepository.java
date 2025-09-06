package ar.utn.ba.ddsi.agregador.domain.repositorie.impl;

import ar.utn.ba.ddsi.agregador.domain.repositorie.ISolicitudesRepository;
import ar.utn.ba.ddsi.agregador.domain.solicitud.SolicitudDeEliminacion;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SolicitudesRepository implements ISolicitudesRepository {

  private final List<SolicitudDeEliminacion> solicitudes = new ArrayList<>();
  private final AtomicLong idGenerator = new AtomicLong(1);

  @Override
  public void save(SolicitudDeEliminacion solicitud) {
    solicitud.setId(idGenerator.getAndIncrement());
    this.solicitudes.add(solicitud);
  }

  @Override
  public List<SolicitudDeEliminacion> findAll() {
    return solicitudes;
  }

  @Override
  public SolicitudDeEliminacion findById(Long id) {
    return solicitudes.stream()
        .filter(s -> s.getId().equals(id))
        .findFirst()
        .orElse(null);
  }

  @Override
  public void eliminar(Long id) {
    solicitudes.removeIf(s -> s.getId().equals(id));
  }

}
