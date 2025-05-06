package ar.edu.utn.frba.dds.domain.user;

import ar.edu.utn.frba.dds.domain.hecho.Hecho;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ar.edu.utn.frba.dds.domain.request.SolicitudDeEliminacion;

@Getter
@Setter
@NoArgsConstructor
public class Contribuyente {
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