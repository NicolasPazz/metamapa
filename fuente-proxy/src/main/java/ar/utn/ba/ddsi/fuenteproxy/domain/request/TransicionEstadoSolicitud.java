package ar.utn.ba.ddsi.fuenteproxy.domain.request;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TransicionEstadoSolicitud {
  EstadoSolicitud estadoSolicitud;
  LocalDate fecha;

  public TransicionEstadoSolicitud(EstadoSolicitud estadoSolicitud) {
    this.estadoSolicitud = estadoSolicitud;
    this.fecha = LocalDate.now();
  }
}
