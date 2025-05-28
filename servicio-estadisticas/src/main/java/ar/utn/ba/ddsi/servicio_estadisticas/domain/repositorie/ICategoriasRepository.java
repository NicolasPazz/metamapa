package ar.utn.ba.ddsi.servicio_estadisticas.domain.repositorie;

import ar.utn.ba.ddsi.servicio_estadisticas.domain.hecho.Categoria;

public interface ICategoriasRepository {
  public Categoria findById(Long id);
}
