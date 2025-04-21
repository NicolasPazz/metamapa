package ar.edu.utn.frba.dds.domain.model.hecho;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DataHecho {

    @CsvBindByName(column = "Título")
    private String titulo;

    @CsvBindByName(column = "Descripción")
    private String descripcion;

    @CsvBindByName(column = "Categoría")
    private String categoria;

    @CsvBindByName(column = "Latitud")
    private Double latitud;

    @CsvBindByName(column = "Longitud")
    private Double longitud;

    @CsvDate("dd/MM/yyyy")
    @CsvBindByName(column = "Fecha del hecho")
    private LocalDate fecha;
}
