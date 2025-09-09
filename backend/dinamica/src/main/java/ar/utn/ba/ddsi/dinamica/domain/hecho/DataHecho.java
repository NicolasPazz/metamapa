package ar.utn.ba.ddsi.dinamica.domain.hecho;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataHecho {
  private String titulo;
  private String descripcion;
  private String nombreCategoria;
  private Double latitud;
  private Double longitud;
  private LocalDate fechaDeAcontecimiento;
  @Builder.Default
  private LocalDateTime fechaDeCarga = LocalDateTime.now();
}