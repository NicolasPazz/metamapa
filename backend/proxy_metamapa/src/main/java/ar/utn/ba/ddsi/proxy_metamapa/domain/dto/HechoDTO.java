package ar.utn.ba.ddsi.proxy_metamapa.domain.dto;

import ar.utn.ba.ddsi.proxy_metamapa.domain.enums.EstadoHecho;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
    "fecha_acontecimiento",
    "fecha_carga",
    "origen",
    "estado",
    "contribuyente_id",
    "etiquetas",
    "multimedia",
    "transiciones"
})
public class HechoDTO {
  private Long id;
  private String origen;
  private EstadoHecho estado;
  @JsonProperty("contribuyente_id")
  private Long contribuyenteId;
  private List<String> etiquetas;
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;
  @JsonProperty("fecha_acontecimiento")
  private LocalDateTime fechaDeAcontecimiento;
  @JsonProperty("fecha_carga")
  private LocalDateTime fechaDeCarga;
  @JsonProperty("multimedia")
  private List<ContenidoMultimediaDTO> multimedia;
  @JsonProperty("transiciones")
  private List<TransicionEstadoHechoDTO> transiciones;

  public HechoDTO(Long contribuyenteId, List<String> etiquetas, String titulo, String descripcion, String categoria, Double latitud, Double longitud, LocalDateTime fechaDeAcontecimiento, LocalDateTime fechaDeCarga, List<ContenidoMultimediaDTO> multimedia, List<TransicionEstadoHechoDTO> transiciones) {
    this.id = null;
    this.origen = "METAMAPA";
    this.estado = EstadoHecho.VISIBLE;
    this.contribuyenteId = contribuyenteId;
    this.etiquetas = etiquetas;
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.categoria = categoria;
    this.latitud = latitud;
    this.longitud = longitud;
    this.fechaDeAcontecimiento = fechaDeAcontecimiento;
    this.fechaDeCarga = fechaDeCarga;
    this.multimedia = multimedia;
    this.transiciones = transiciones;
  }
}