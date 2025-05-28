package ar.utn.ba.ddsi.estadisticas.domain.repositorie;

import ar.utn.ba.ddsi.estadisticas.domain.hecho.Categoria;

public interface ICategoriasRepository {
  public Categoria findById(Long id);
}
