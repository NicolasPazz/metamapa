package ar.utn.ba.ddsi.proxy_metamapa.domain.repositorie;

import ar.utn.ba.ddsi.proxy_metamapa.domain.hecho.Categoria;

public interface ICategoriasRepository {
  public Categoria findById(Long id);
}
