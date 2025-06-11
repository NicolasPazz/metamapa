package ar.utn.ba.ddsi.proxy_metamapa.domain.dto.output;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
@JsonPropertyOrder({
    "id",
    "titulo",
    "descripcion",
    "categoria",
    "latitud",
    "longitud",
    "fechaDeAcontecimiento",
    "fechaDeCarga",
    "ultimaModificacion",
    "origen",
    "estado",
    "contribuyente",
    "etiquetas",
    "contenidoMultimedia",
    "transicionesDeEstado"
})
public class HechoDTO {
  private Long id;
  private String origen;
  private String estado;
  private Long contribuyente;
  private List<String> etiquetas;
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;
  private LocalDate fechaDeAcontecimiento;
  private LocalDateTime fechaDeCarga;
  private LocalDateTime ultimaModificacion;
  private List<ContenidoMultimediaDTO> contenidoMultimedia;
  private List<TransicionEstadoHechoDTO> transicionesDeEstado;

  public HechoDTO(Long contribuyente, List<String> etiquetas, String titulo, String descripcion, String categoria, Double latitud, Double longitud, LocalDate fechaDeAcontecimiento, LocalDateTime fechaDeCarga, LocalDateTime ultimaModificacion, List<ContenidoMultimediaDTO> contenidoMultimedia, List<TransicionEstadoHechoDTO> transicionesDeEstado) {
    this.id = null;
    this.origen = "METAMAPA";
    this.estado = "VISIBLE";
    this.contribuyente = contribuyente;
    this.etiquetas = etiquetas;
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.categoria = categoria;
    this.latitud = latitud;
    this.longitud = longitud;
    this.fechaDeAcontecimiento = fechaDeAcontecimiento;
    this.fechaDeCarga = fechaDeCarga;
    this.ultimaModificacion = ultimaModificacion;
    this.contenidoMultimedia = contenidoMultimedia;
    this.transicionesDeEstado = transicionesDeEstado;
  }
}