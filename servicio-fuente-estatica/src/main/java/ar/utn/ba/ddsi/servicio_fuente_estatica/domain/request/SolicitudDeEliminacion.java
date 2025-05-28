package ar.utn.ba.ddsi.servicio_fuente_estatica.domain.request;

import ar.utn.ba.ddsi.servicio_fuente_estatica.domain.hecho.Hecho;
import ar.utn.ba.ddsi.servicio_fuente_estatica.domain.request.EstadoSolicitud;
import ar.utn.ba.ddsi.servicio_fuente_estatica.domain.request.TransicionEstadoSolicitud;
import ar.utn.ba.ddsi.servicio_fuente_estatica.domain.request.validacion.Validacion;
import ar.utn.ba.ddsi.servicio_fuente_estatica.domain.user.Contribuyente;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class SolicitudDeEliminacion {
  private Long id;
  private Hecho hecho;
  private String textoFundamentos;
  private LocalDate fecha;
  private ar.utn.ba.ddsi.servicio_fuente_estatica.domain.request.EstadoSolicitud estado;
  private Contribuyente contribuyente;
  private List<TransicionEstadoSolicitud> transicionesDeEstado;
  private Set<Validacion> validaciones;

  public SolicitudDeEliminacion(Hecho hecho, String textoFundamentos, Contribuyente contribuyente, Set<Validacion> validaciones) {
    this.hecho = hecho;
    this.textoFundamentos = textoFundamentos;
    this.fecha = LocalDate.now();
    this.estado = ar.utn.ba.ddsi.servicio_fuente_estatica.domain.request.EstadoSolicitud.PENDIENTE;
    this.contribuyente = contribuyente;
    this.validaciones = validaciones;
  }

  public void aceptar() {
    transicionesDeEstado.add(new TransicionEstadoSolicitud(ar.utn.ba.ddsi.servicio_fuente_estatica.domain.request.EstadoSolicitud.ACEPTADA));
    this.estado = ar.utn.ba.ddsi.servicio_fuente_estatica.domain.request.EstadoSolicitud.ACEPTADA;
    hecho.ocultar();
  }

  public void rechazar() {
    transicionesDeEstado.add(new TransicionEstadoSolicitud(ar.utn.ba.ddsi.servicio_fuente_estatica.domain.request.EstadoSolicitud.RECHAZADA));
    this.estado = EstadoSolicitud.RECHAZADA;
  }
}
