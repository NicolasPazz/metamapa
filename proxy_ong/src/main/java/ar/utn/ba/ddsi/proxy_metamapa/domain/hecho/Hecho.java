package ar.utn.ba.ddsi.proxy_metamapa.domain.hecho;

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
  private Double latitud;
  private Double longitud;
  private String fechaDeAcontecimiento;
  private String fechaDeCarga;
  private String ultimaModificacion;

  public Hecho(Long id, String titulo, String descripcion, Double latitud, Double longitud, String fechaDeAcontecimiento, String fechaDeCarga, String ultimaModificacion) {
    this.id = id;
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.latitud = latitud;
    this.longitud = longitud;
    this.fechaDeAcontecimiento = fechaDeAcontecimiento;
    this.fechaDeCarga = fechaDeCarga;
    this.ultimaModificacion = ultimaModificacion;
  }
}