package ar.utn.ba.ddsi.agregador.domain.hecho;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataHecho {
  private String titulo;
  private String descripcion;
  private Double latitud;
  private Double longitud;
  private LocalDate fechaDeAcontecimiento;
  private LocalDateTime fechaDeCarga = LocalDateTime.now();
}