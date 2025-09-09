package ar.utn.ba.ddsi.estatica.domain.repositories;

import ar.utn.ba.ddsi.estatica.domain.hecho.Hecho;
import java.util.List;

public interface IHechosRepository {
  Hecho findById(Long id);
  List<Hecho> findAll();
  void save(Hecho hecho);
  void update(Hecho hecho);
  void delete(Hecho hecho);
}
