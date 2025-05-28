package ar.utn.ba.ddsi.servicio_agregador.domain.repositorie.impl;

import ar.utn.ba.ddsi.servicio_agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.servicio_agregador.domain.repositorie.IHechosRepository;
import java.util.ArrayList;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class HechosRepository implements IHechosRepository {
  private List<Hecho> hechos;

  public HechosRepository() {
    this.hechos = new ArrayList<>();
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
    this.hechos.add(hecho);
    hecho.setId((long) this.hechos.size());
  }

  @Override
  public void delete(Hecho hecho) {
    this.hechos.remove(hecho);
  }
}
