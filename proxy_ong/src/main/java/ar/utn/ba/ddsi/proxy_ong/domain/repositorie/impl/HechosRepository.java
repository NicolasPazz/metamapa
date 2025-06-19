package ar.utn.ba.ddsi.proxy_ong.domain.repositorie.impl;

import ar.utn.ba.ddsi.proxy_ong.domain.hecho.Hecho;
import java.util.ArrayList;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class HechosRepository {
  private List<Hecho> hechos = new ArrayList<>();

  public Hecho findById(Long id) {
    return this.hechos.stream()
        .filter(h -> h.getId().equals(id))
        .findFirst()
        .orElse(null);
  }

  public void save(Hecho hecho) {
    if (findById(hecho.getId()) == null) {
      this.hechos.add(hecho);
    }
  }

  public boolean existsById(Long id) {
    return this.hechos.stream().anyMatch(h -> h.getId().equals(id));
  }
}