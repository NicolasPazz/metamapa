package ar.edu.utn.frba.dds.model.domain.request;

import ar.edu.utn.frba.dds.model.domain.hecho.Hecho;
import lombok.Getter;
import lombok.Setter;
import ar.edu.utn.frba.dds.model.domain.user.Contribuyente;
import java.time.LocalDateTime;

@Getter
@Setter
public class SolicitudDeEliminacion {
  private Hecho hecho;
  private String textoFundamentos;
  private LocalDateTime fecha;
  private Contribuyente contribuyente;
  private EstadoSolicitud estado;

  public SolicitudDeEliminacion(Hecho hecho, String textoFundamentos, LocalDateTime fecha, Contribuyente contribuyente) {
    this.hecho = hecho;
    this.textoFundamentos = textoFundamentos;
    this.fecha = fecha;
    this.contribuyente = contribuyente;
    this.estado = EstadoSolicitud.PENDIENTE;
  }

  public void aceptar() {
    // TODO
  }

  public void rechazar() {
    // TODO
  }
}
