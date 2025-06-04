package ar.utn.ba.ddsi.dinamica.domain.repositorie.impl;

import ar.utn.ba.ddsi.dinamica.domain.hecho.Hecho;
import ar.utn.ba.ddsi.dinamica.domain.repositorie.IHechosRepository;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
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
  public Hecho findByIdSolicitud(Long id) {
    return this.hechos.stream()
            .filter(h -> h.getIdSolicitud().equals(id))
            .findFirst()
            .orElse(null);
  }

  @Override
  public List<Hecho> findAll() {
    return this.hechos;
  }

  @Override
  public void save(Hecho hecho) {
    hecho.setId((long) (this.hechos.size() + 1));
    this.hechos.add(hecho);
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
