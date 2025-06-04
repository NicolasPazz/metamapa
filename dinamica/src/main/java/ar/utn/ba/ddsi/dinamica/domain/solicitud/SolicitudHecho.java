package ar.utn.ba.ddsi.dinamica.domain.solicitud;

import ar.utn.ba.ddsi.dinamica.domain.solicitud.EstadoSolicitud;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class SolicitudHecho {
    private Long idSolicitud;
    private Long idContribuyente;
    private String titulo;
    private String descripcion;
    private String categoria;
    private double latitud;
    private double longitud;
    private LocalDate fechaDeAcontecimiento;
    private List<String> multimedia;
    private LocalDateTime fechaDeCargaSolicitud;
    private EstadoSolicitud estado; //pendiente-aceptado-rechazado
    private String sugerencia;
    private List<TransicionEstadoSolicitud> transiciones;

}
