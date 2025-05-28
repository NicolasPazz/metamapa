package ar.utn.ba.ddsi.fuenteestatica.domain.repositorie.impl;

import ar.utn.ba.ddsi.fuenteestatica.domain.hecho.Categoria;
import ar.utn.ba.ddsi.fuenteestatica.domain.repositorie.ICategoriasRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class CategoriasRepository implements ICategoriasRepository {
  private List<Categoria> categorias;

  @Override
  public Categoria findById(Long id) {
    return this.categorias.stream()
        .filter(c -> c.getId().equals(id))
        .findFirst()
        .orElse(null);
  }
}
