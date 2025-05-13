package ar.edu.utn.frba.dds.domain.repositorie.impl;

import ar.edu.utn.frba.dds.domain.hecho.Hecho;
import ar.edu.utn.frba.dds.domain.repositorie.IHechosRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class HechosRepository implements IHechosRepository {
  private List<Hecho> hechos;

  @Override
  public Hecho findById(Long id) {
    return this.hechos.stream()
        .filter(hecho -> hecho.getId().equals(id))
        .findFirst()
        .orElse(null);
  }
}
