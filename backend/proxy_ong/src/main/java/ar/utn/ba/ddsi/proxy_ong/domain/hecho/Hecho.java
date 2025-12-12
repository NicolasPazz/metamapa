package ar.utn.ba.ddsi.proxy_ong.domain.hecho;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Hecho {
  private Long id;
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;
  @JsonProperty("fecha_acontecimiento")
  private LocalDate fechaDeAcontecimiento;
  @JsonProperty("fecha_carga")
  private LocalDateTime fechaDeCarga;
  private LocalDateTime ultimaModificacion;

  public Hecho(Long id, String titulo, String descripcion, String categoria, Double latitud, Double longitud, LocalDate fechaDeAcontecimiento, LocalDateTime fechaDeCarga, LocalDateTime ultimaModificacion) {
    this.id = id;
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.categoria = categoria;
    this.latitud = latitud;
    this.longitud = longitud;
    this.fechaDeAcontecimiento = fechaDeAcontecimiento;
    this.fechaDeCarga = fechaDeCarga;
    this.ultimaModificacion = ultimaModificacion;
  }
}