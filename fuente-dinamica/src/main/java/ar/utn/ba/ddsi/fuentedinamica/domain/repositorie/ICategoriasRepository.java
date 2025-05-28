package ar.utn.ba.ddsi.fuentedinamica.domain.repositorie;

import ar.utn.ba.ddsi.fuentedinamica.domain.hecho.Categoria;

public interface ICategoriasRepository {
  public Categoria findById(Long id);
}
