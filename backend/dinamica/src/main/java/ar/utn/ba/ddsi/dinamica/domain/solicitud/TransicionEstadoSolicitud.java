package ar.utn.ba.ddsi.dinamica.domain.solicitud;

import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@Embeddable
public class TransicionEstadoSolicitud {
    EstadoSolicitud estadoSolicitud;
    LocalDate fecha;

    public TransicionEstadoSolicitud(EstadoSolicitud estadoSolicitud) {
        this.estadoSolicitud = estadoSolicitud;
        this.fecha = LocalDate.now();
    }
}
