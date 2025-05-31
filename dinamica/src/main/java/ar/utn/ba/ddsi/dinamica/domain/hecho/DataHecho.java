package ar.utn.ba.ddsi.dinamica.domain.hecho;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataHecho {

    //@CsvBindByName(column = "Título")
    private String titulo;

//    @CsvBindByName(column = "Descripción")
    private String descripcion;

//    @CsvBindByName(column = "Categoría")
    private String nombreCategoria;

//    @CsvBindByName(column = "Latitud")
    private Double latitud;

//    @CsvBindByName(column = "Longitud")
    private Double longitud;

//    @CsvDate("dd/MM/yyyy")
//    @CsvBindByName(column = "Fecha del hecho")
    private LocalDate fechaDeAcontecimiento;

    private LocalDateTime fechaDeCarga = LocalDateTime.now();   //mas preciso que sea LocalDateTime
}