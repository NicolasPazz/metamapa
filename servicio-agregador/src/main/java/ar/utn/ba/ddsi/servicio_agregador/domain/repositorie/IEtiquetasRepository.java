package ar.utn.ba.ddsi.servicio_agregador.domain.repositorie;

import ar.utn.ba.ddsi.servicio_agregador.domain.hecho.Etiqueta;
import java.util.List;
import java.util.Set;

public interface IEtiquetasRepository {
  public Etiqueta findById(Long id);
  public List<Etiqueta> findAll();
  public Set<Etiqueta> findAllById(List<Long> idEtiquetas);
  public void save(Etiqueta etiqueta);
  public void delete(Etiqueta etiqueta);
}
