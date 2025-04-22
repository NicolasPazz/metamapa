package ar.edu.utn.frba.dds.domain.model.request;

import ar.edu.utn.frba.dds.domain.model.hecho.Hecho;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class SolicitudDeEliminacion {
  private Hecho hecho;
  private String textoFundamentos;
  private LocalDate fecha;
  private EstadoSolicitud estado;

  public SolicitudDeEliminacion(Hecho hecho, String textoFundamentos) {
    if (textoFundamentos.length() < 500) {
      throw new IllegalArgumentException("Los fundamentos deben tener al menos 500 caracteres");
    }
    this.hecho = hecho;
    this.textoFundamentos = textoFundamentos;
    this.fecha = LocalDate.now();
    this.estado = EstadoSolicitud.PENDIENTE;
  }

  public void aceptar() {
    this.estado = EstadoSolicitud.ACEPTADA;
    hecho.ocultar();
  }

  public void rechazar() {
    this.estado = EstadoSolicitud.RECHAZADA;
  }
}
