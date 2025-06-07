package ar.utn.ba.ddsi.agregador.domain.repositorie.impl;

import ar.utn.ba.ddsi.agregador.domain.hecho.*;
import ar.utn.ba.ddsi.agregador.domain.repositorie.IColeccionesRepository;
import ar.utn.ba.ddsi.agregador.domain.repositorie.IHechosRepository;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

@Repository
public class HechosRepository implements IHechosRepository {

  private List<Hecho> hechos;
  private final AtomicLong idGenerator;

  private final IColeccionesRepository coleccionesRepository;

  public HechosRepository(IColeccionesRepository coleccionesRepository) {
    this.hechos = new ArrayList<>();
    this.idGenerator = new AtomicLong(1);
    this.coleccionesRepository = coleccionesRepository;
  }

  @Override
  public Hecho findById(Long id) {
    return this.hechos.stream()
        .filter(h -> h.getId().equals(id))
        .findFirst()
        .orElse(null);
  }

  @Override
  public List<Hecho> findAll() {
    return this.hechos;
  }

  @Override
  public void save(Hecho hecho) {
    hecho.setId(idGenerator.getAndIncrement());
    this.hechos.add(hecho);
    coleccionesRepository.actualizarColecciones();
  }

  @Override
  public void saveBatch(List<Hecho> nuevosHechos) {
    if (nuevosHechos == null || nuevosHechos.isEmpty()) return;

    for (Hecho hecho : nuevosHechos) {
      hecho.setId(idGenerator.getAndIncrement());
      this.hechos.add(hecho);
    }
    coleccionesRepository.actualizarColecciones();
  }

  @Override
  public void update(Hecho hecho) {
    for (int i = 0; i < hechos.size(); i++) {
      if (hechos.get(i).getId().equals(hecho.getId())) {
        hechos.set(i, hecho);
        return;
      }
    }
  }

  @Override
  public void delete(Hecho hecho) {
    this.hechos.remove(hecho);
  }
}
