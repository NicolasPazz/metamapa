package ar.utn.frba.dds.metamapa.domain.repositorie;

import ar.utn.frba.dds.metamapa.domain.hecho.Categoria;

public interface ICategoriasRepository {
  public Categoria findById(Long id);
}
