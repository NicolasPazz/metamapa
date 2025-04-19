package ar.edu.utn.frba.dds.model.domain.user;

import ar.edu.utn.frba.dds.model.domain.hecho.Hecho;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ar.edu.utn.frba.dds.model.domain.request.SolicitudDeEliminacion;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Contribuyente {
  private String nombre;
  private String apellido;
  private Integer edad;

  public Contribuyente(String nombre, String apellido, Integer edad) {
    this.nombre = nombre;
    this.apellido = apellido;
    this.edad = edad;
  }

  public void subir(Hecho hecho) {
    // TODO
  }

  public SolicitudDeEliminacion solicitarEliminacion(Hecho hecho, String fundamentos) {
    return new SolicitudDeEliminacion(hecho, fundamentos);
  }
}
