package ar.utn.ba.ddsi.agregador.domain.repositories.impl;

import ar.utn.ba.ddsi.agregador.domain.hecho.*;
import ar.utn.ba.ddsi.agregador.domain.repositories.IHechosRepository;
import ar.utn.ba.ddsi.agregador.user.Contribuyente;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
