package ar.utn.ba.ddsi.proxy_metamapa.domain.repositorie.impl;

import ar.utn.ba.ddsi.proxy_metamapa.domain.hecho.Categoria;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class CategoriasRepository {
  private List<Categoria> categorias = new ArrayList<>();

  public Categoria findByName(String nombre) {
    return this.categorias.stream()
        .filter(c -> c.getNombre().equals(nombre.trim().toLowerCase()))
        .findFirst()
        .orElse(null);
  }

  public void save(Categoria categoria) {
    if (findByName(categoria.getNombre()) == null) {
      this.categorias.add(categoria);
    }
  }
}
