package ar.edu.utn.frba.dds.model.domain.hecho;

import lombok.Getter;
import lombok.Setter;
import com.opencsv.bean.CsvBindByName;

import java.time.LocalDateTime;

@Getter
@Setter
public class DataHecho {
    @CsvBindByName(column = "Título")
    private String titulo;
    @CsvBindByName(column = "Descripción")
    private String descripcion;
    @CsvBindByName(column = "Categoría")
    private String categoria;
    @CsvBindByName(column = "Fecha")
    private LocalDateTime fecha;
    @CsvBindByName(column = "Latitud")
    private Double latitud;
    @CsvBindByName(column = "Longitud")
    private Double longitud;
}
