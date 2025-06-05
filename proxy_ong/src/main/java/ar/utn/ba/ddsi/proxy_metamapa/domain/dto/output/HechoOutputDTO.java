package ar.utn.ba.ddsi.proxy_metamapa.domain.dto.output;

import java.util.List;
import lombok.Data;

@Data
public class HechoOutputDTO {
  private Long id;
  private String origen;
  private String estado;
  private String contribuyente;
  private List<String> etiquetas;
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;
  private String fechaDeAcontecimiento;
  private String fechaDeCarga;
  private String ultimaModificacion;
  private List<String> contenidoMultimedia;
  private List<String> transicionesDeEstado;

  public HechoOutputDTO(
      Long id,
      String titulo,
      String descripcion,
      String categoria,
      Double latitud,
      Double longitud,
      String fechaDeAcontecimiento,
      String fechaDeCarga,
      String ultimaModificacion
  ) {
    this.id = id;
    this.origen = "ONG";
    this.estado = "VISIBLE";
    this.contribuyente = null;
    this.etiquetas = null;
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.categoria = categoria;
    this.latitud = latitud;
    this.longitud = longitud;
    this.fechaDeAcontecimiento = fechaDeAcontecimiento;
    this.fechaDeCarga = fechaDeCarga;
    this.ultimaModificacion = ultimaModificacion;
    this.contenidoMultimedia = null;
    this.transicionesDeEstado = null;
  }
}