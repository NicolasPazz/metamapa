package ar.utn.ba.ddsi.monomodule.domain.user;

import ar.utn.ba.ddsi.monomodule.domain.hecho.Hecho;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Contribuyente {
  private Long id;
  private String nombre;
  private String apellido;
  private Integer edad;

  public Contribuyente(String nombre, String apellido, Integer edad) {
    if (nombre == null) {
      throw new IllegalArgumentException("El nombre del contribuyente es obligatorio.");
    }
    this.nombre = nombre;
    this.apellido = apellido;
    this.edad = edad;
  }

  public boolean esAnonimo() {
    return this.nombre != null;
  }

  public void subirHecho(Hecho hecho) {
    // TODO
    if (!this.esAnonimo()) {
      // TODO: almacenar datos personales y poner a disposicion de administradores
    }
  }
}