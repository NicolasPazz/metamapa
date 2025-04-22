package ar.edu.utn.frba.dds.domain.model.hecho;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

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
    private LocalDate fechaDeAcontecimiento;

    private LocalDate fechaDeCarga = LocalDate.now();
}