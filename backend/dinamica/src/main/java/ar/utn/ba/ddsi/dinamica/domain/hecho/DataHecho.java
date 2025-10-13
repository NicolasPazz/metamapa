package ar.utn.ba.ddsi.dinamica.domain.hecho;

import jakarta.persistence.Column;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataHecho {
  @Column(name = "titulo")
  private String titulo;
  @Column(name = "descripcion")
  private String descripcion;
  @Column(name = "categoria")
  private String nombreCategoria;
  @Column(name = "latitud")
  private Double latitud;
  @Column(name = "longitud")
  private Double longitud;
  @Column(name = "fecha_de_acontecimiento")
  private LocalDate fechaDeAcontecimiento;
  @Column(name = "fecha_de_carga")
  @Builder.Default
  private LocalDateTime fechaDeCarga = LocalDateTime.now();
}