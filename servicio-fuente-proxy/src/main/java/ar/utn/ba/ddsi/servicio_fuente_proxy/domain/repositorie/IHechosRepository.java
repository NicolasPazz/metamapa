package ar.utn.ba.ddsi.servicio_fuente_proxy.domain.repositorie;

import ar.utn.ba.ddsi.servicio_fuente_proxy.domain.hecho.Hecho;
import java.util.List;

public interface IHechosRepository {
  public Hecho findById(Long id);
  public List<Hecho> findAll();
  public void save(Hecho hecho);
  public void delete(Hecho hecho);
}
