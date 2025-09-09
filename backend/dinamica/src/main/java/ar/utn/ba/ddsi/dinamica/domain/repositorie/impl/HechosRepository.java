package ar.utn.ba.ddsi.dinamica.domain.repositorie.impl;

import ar.utn.ba.ddsi.dinamica.domain.hecho.Hecho;
import ar.utn.ba.ddsi.dinamica.domain.repositorie.IHechosRepository;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class HechosRepository implements IHechosRepository {

  private final List<Hecho> hechos = new ArrayList<>();
  private final AtomicLong idGenerator = new AtomicLong(1);


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
    hecho.setId(idGenerator.getAndIncrement());
    hecho.setUltimaActualizacion(LocalDateTime.now());
    this.hechos.add(hecho);

  }

  @Override
  public void update(Hecho hecho) {
    for (int i = 0; i < hechos.size(); i++) {
      if (hechos.get(i).getId().equals(hecho.getId())) {
        hecho.setUltimaActualizacion(LocalDateTime.now());
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
