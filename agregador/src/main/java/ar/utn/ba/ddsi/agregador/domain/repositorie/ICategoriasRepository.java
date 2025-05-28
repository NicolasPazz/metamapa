package ar.utn.ba.ddsi.agregador.domain.repositorie;

import ar.utn.ba.ddsi.agregador.domain.hecho.Categoria;

public interface ICategoriasRepository {
  public Categoria findById(Long id);
}
