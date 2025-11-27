package ar.utn.ba.ddsi.agregador.domain.dto.input;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data // Usando Lombok para getters/setters
public class HechosFiltroDTO {
    private String categoria;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate fecha_reporte_desde;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate fecha_reporte_hasta;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate fecha_acontecimiento_desde;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate fecha_acontecimiento_hasta;

    private String ubicacion;
    private String modo;
}