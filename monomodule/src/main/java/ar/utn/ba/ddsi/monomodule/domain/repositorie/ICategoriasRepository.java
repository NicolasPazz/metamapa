package ar.utn.ba.ddsi.monomodule.domain.repositorie;

import ar.utn.ba.ddsi.monomodule.domain.hecho.Categoria;

public interface ICategoriasRepository {
  public Categoria findById(Long id);
}
