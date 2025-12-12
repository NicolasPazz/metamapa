package ar.utn.ba.ddsi.estatica.domain.dto.output;

import ar.utn.ba.ddsi.estatica.domain.hecho.OrigenHecho;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class HechoOutputDTO {
  private Long id;
  private OrigenHecho origen;
  private String estado;
  private String contribuyente;
  private List<String> etiquetas;
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
  private List<String> contenidoMultimedia;
  private List<String> transicionesDeEstado;

  public HechoOutputDTO(
      Long id,
      String titulo,
      String descripcion,
      String categoria,
      Double latitud,
      Double longitud,
      LocalDate fechaDeAcontecimiento,
      LocalDateTime fechaDeCarga
  ) {
    this.id = id;
    this.origen = OrigenHecho.DATASET;
    this.estado = "VISIBLE";
    this.contribuyente = null;
    this.etiquetas = new ArrayList<>();
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.categoria = categoria;
    this.latitud = latitud;
    this.longitud = longitud;
    this.fechaDeAcontecimiento = fechaDeAcontecimiento;
    this.fechaDeCarga = fechaDeCarga;
    this.ultimaModificacion = null;
    this.contenidoMultimedia = new ArrayList<>();
    this.transicionesDeEstado = new ArrayList<>();
  }
}