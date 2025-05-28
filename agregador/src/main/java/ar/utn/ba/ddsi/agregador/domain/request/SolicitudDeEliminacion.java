package ar.utn.ba.ddsi.agregador.domain.request;

import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.domain.request.validacion.Validacion;
import ar.utn.ba.ddsi.agregador.domain.user.Contribuyente;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SolicitudDeEliminacion {
  private Long id;
  private Hecho hecho;
  private String textoFundamentos;
  private LocalDate fecha;
  private EstadoSolicitud estado;
  private Contribuyente contribuyente;
  private List<TransicionEstadoSolicitud> transicionesDeEstado;
  private Set<Validacion> validaciones;

  public SolicitudDeEliminacion(Hecho hecho, String textoFundamentos, Contribuyente contribuyente, Set<Validacion> validaciones) {
    this.hecho = hecho;
    this.textoFundamentos = textoFundamentos;
    this.fecha = LocalDate.now();
    this.estado = EstadoSolicitud.PENDIENTE;
    this.contribuyente = contribuyente;
    this.validaciones = validaciones;
  }

  public void aceptar() {
    transicionesDeEstado.add(new TransicionEstadoSolicitud(EstadoSolicitud.ACEPTADA));
    this.estado = EstadoSolicitud.ACEPTADA;
    hecho.ocultar();
  }

  public void rechazar() {
    transicionesDeEstado.add(new TransicionEstadoSolicitud(EstadoSolicitud.RECHAZADA));
    this.estado = EstadoSolicitud.RECHAZADA;
  }
}
