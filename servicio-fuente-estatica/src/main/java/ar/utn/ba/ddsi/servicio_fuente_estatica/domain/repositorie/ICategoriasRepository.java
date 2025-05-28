package ar.utn.ba.ddsi.servicio_fuente_estatica.domain.repositorie;

import ar.utn.ba.ddsi.servicio_fuente_estatica.domain.hecho.Categoria;

public interface ICategoriasRepository {
  public Categoria findById(Long id);
}
