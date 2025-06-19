package ar.utn.ba.ddsi.dinamica.domain.solicitud;

import java.time.LocalDate;

public class TransicionEstadoSolicitud {
    EstadoSolicitud estadoSolicitud;
    LocalDate fecha;

    public TransicionEstadoSolicitud(EstadoSolicitud estadoSolicitud) {
        this.estadoSolicitud = estadoSolicitud;
        this.fecha = LocalDate.now();
    }
}
