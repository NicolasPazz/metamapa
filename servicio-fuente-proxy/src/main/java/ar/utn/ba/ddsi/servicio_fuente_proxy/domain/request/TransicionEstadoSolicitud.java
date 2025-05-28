package ar.utn.ba.ddsi.servicio_fuente_proxy.domain.request;

import ar.utn.ba.ddsi.servicio_fuente_proxy.domain.request.EstadoSolicitud;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Setter
@Getter
public class TransicionEstadoSolicitud {
  ar.utn.ba.ddsi.servicio_fuente_proxy.domain.request.EstadoSolicitud estadoSolicitud;
  LocalDate fecha;

  public TransicionEstadoSolicitud(EstadoSolicitud estadoSolicitud) {
    this.estadoSolicitud = estadoSolicitud;
    this.fecha = LocalDate.now();
  }
}
