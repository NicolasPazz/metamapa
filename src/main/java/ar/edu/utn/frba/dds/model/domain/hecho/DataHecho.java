package ar.edu.utn.frba.dds.model.domain.hecho;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DataHecho {
    private String titulo;
    private String descripcion;
    private String categoria;
    private LocalDateTime fecha;
}
