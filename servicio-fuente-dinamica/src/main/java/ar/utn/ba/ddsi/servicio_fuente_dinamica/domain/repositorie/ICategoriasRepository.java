package ar.utn.ba.ddsi.servicio_fuente_dinamica.domain.repositorie;

import ar.utn.ba.ddsi.servicio_fuente_dinamica.domain.hecho.Categoria;

public interface ICategoriasRepository {
  public Categoria findById(Long id);
}
