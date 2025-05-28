package ar.utn.ba.ddsi.fuenteestatica.domain.repositorie;

import ar.utn.ba.ddsi.fuenteestatica.domain.hecho.Categoria;

public interface ICategoriasRepository {
  public Categoria findById(Long id);
}
