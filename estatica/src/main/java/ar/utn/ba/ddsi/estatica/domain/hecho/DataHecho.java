package ar.utn.ba.ddsi.estatica.domain.hecho;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import jakarta.persistence.Embeddable;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class DataHecho {
  @CsvBindByName(column = "Título")
  private String titulo;

  @CsvBindByName(column = "Descripción")
  private String descripcion;

  @CsvBindByName(column = "Categoría")
  private String nombreCategoria;

  @CsvBindByName(column = "Latitud")
  private Double latitud;

  @CsvBindByName(column = "Longitud")
  private Double longitud;

  @CsvDate("dd/MM/yyyy")
  @CsvBindByName(column = "Fecha del hecho")
  private LocalDate fechaDeAcontecimiento;

  @Builder.Default
  private LocalDateTime fechaDeCarga = LocalDateTime.now();
}