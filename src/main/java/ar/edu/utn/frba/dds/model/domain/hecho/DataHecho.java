package ar.edu.utn.frba.dds.model.domain.hecho;

import lombok.Getter;
import lombok.Setter;
import com.opencsv.bean.CsvBindByName;

import java.time.LocalDateTime;

@Getter
@Setter
public class DataHecho {
    @CsvBindByName
    private String titulo;
    @CsvBindByName
    private String descripcion;
    @CsvBindByName
    private String categoria;
    @CsvBindByName
    private LocalDateTime fecha;
    @CsvBindByName
    private Double latitud;
    @CsvBindByName
    private Double longitud;
}
