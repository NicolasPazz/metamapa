package ar.utn.ba.ddsi.dinamica.domain.user;

import ar.utn.ba.ddsi.dinamica.domain.hecho.Hecho;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ar.utn.ba.ddsi.dinamica.domain.request.SolicitudDeEliminacion;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class Contribuyente {
  private Long id;
  private String nombre;
  private String apellido;
  private LocalDate fechaDeNacimiento;  //mejor esto que edad

  public Contribuyente(String nombre) {
    if (nombre == null) {
      throw new IllegalArgumentException("El nombre del contribuyente es obligatorio.");
    }
    this.nombre = nombre;
  }

  public Contribuyente(String nombre, String apellido, LocalDate fechaDeNacimiento) {
    if (nombre == null) {
      throw new IllegalArgumentException("El nombre del contribuyente es obligatorio.");
    }
    this.nombre = nombre;
    this.apellido = apellido;
    this.fechaDeNacimiento = fechaDeNacimiento;
  }

  public SolicitudDeEliminacion solicitarEliminacion(Hecho hecho, String fundamentos) {
    return new SolicitudDeEliminacion(hecho, fundamentos);
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