package ar.utn.ba.ddsi.frontend.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class TransicionEstadoOutputDTO {
    @JsonProperty("estado")
    private EstadoSolicitud estado;
    private LocalDateTime fecha;
}
