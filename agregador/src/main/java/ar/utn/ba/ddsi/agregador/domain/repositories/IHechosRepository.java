package ar.utn.ba.ddsi.agregador.domain.repositories;

import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import java.util.List;

public interface IHechosRepository {
  public Hecho findById(Long id);
  public List<Hecho> findAll();
  public void save(Hecho hecho);
  void update(Hecho hecho);
  public void delete(Hecho hecho);
}

