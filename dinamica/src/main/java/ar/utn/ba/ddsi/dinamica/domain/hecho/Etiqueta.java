package ar.utn.ba.ddsi.dinamica.domain.hecho;

import lombok.Getter;
import lombok.Setter;
import java.util.Objects;

@Setter
@Getter
public class Etiqueta {
  private Long id;
  private String nombre;

  public Etiqueta(String nombre) {
    this.nombre = nombre.trim().toLowerCase();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Etiqueta etiqueta = (Etiqueta) o;
    return Objects.equals(nombre, etiqueta.nombre);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nombre);
  }
}