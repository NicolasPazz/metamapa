package ar.utn.ba.ddsi.proxy_ong.domain.hecho;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hecho")
public class Hecho {
  @Id
  private Long id;

  @Column(length = 500)
  private String titulo;

  @Column(length = 2000)
  private String descripcion;

  @Column(length = 100)
  private String categoria;

  private Double latitud;

  private Double longitud;

  @Column(name = "fecha_acontecimiento")
  private LocalDateTime fechaDeAcontecimiento;

  @Column(name = "fecha_carga")
  private LocalDateTime fechaDeCarga;

  @Column(name = "ultima_modificacion")
  private LocalDateTime ultimaModificacion;
}