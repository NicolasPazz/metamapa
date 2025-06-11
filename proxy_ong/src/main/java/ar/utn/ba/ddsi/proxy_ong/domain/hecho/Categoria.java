package ar.utn.ba.ddsi.proxy_ong.domain.hecho;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Categoria {
  private String nombre;

  public Categoria(String nombre) {
    this.nombre = nombre.trim().toLowerCase();
  }
}