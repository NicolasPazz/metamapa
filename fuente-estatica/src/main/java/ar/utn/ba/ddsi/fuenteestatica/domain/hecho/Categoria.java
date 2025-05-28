package ar.utn.ba.ddsi.fuenteestatica.domain.hecho;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class Categoria {
  private Long id;
  private String nombre;

  public Categoria(String nombre) {
    this.nombre = nombre.trim().toLowerCase();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Categoria categoria = (Categoria) o;
    return nombre.equals(categoria.nombre);
  }

  @Override
  public int hashCode() {
    return nombre.hashCode();
  }
}
