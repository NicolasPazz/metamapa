package ar.edu.utn.frba.dds.model.domain.hecho;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class Categoria {
  private String nombre;

  public Categoria(String nombre) {
    this.nombre = nombre;
  }
}
