package ar.utn.ba.ddsi.servicio_agregador.domain.repositorie;

import ar.utn.ba.ddsi.servicio_agregador.domain.hecho.Categoria;

public interface ICategoriasRepository {
  public Categoria findById(Long id);
}
