package ar.utn.ba.ddsi.agregador.domain.hecho;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class DataHecho {
  @Column(name = "titulo")
  private String titulo;
  @Column(name = "descripcion")
  private String descripcion;
  @Column(name = "latitud")
  private Double latitud;
  @Column(name = "longitud")
  private Double longitud;
  @Column(name = "fecha_de_acontecimiento")
  private LocalDate fechaDeAcontecimiento;
  @Column(name = "fecha_de_carga")
  private LocalDateTime fechaDeCarga = LocalDateTime.now();
}