package ar.utn.ba.ddsi.servicio_fuente_dinamica.domain.request;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

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
