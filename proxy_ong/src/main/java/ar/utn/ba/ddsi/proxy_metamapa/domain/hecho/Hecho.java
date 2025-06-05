package ar.utn.ba.ddsi.proxy_metamapa.domain.hecho;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ar.utn.ba.ddsi.proxy_metamapa.domain.hecho.Categoria;

@Getter
@Setter
@NoArgsConstructor
public class Hecho {
  private Long id;
  private String titulo;
  private String descripcion;
  private Categoria categoria;
  private Double latitud;
  private Double longitud;
  private String fechaDeAcontecimiento;
  private String fechaDeCarga;
  private String ultimaModificacion;

  public Hecho(Long id, String titulo, String descripcion, Categoria categoria, Double latitud, Double longitud, String fechaDeAcontecimiento, String fechaDeCarga, String ultimaModificacion) {
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