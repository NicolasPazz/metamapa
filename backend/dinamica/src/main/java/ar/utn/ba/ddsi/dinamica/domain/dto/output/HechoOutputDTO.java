package ar.utn.ba.ddsi.dinamica.domain.dto.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;


@JsonPropertyOrder({
    "id", "titulo", "descripcion", "categoria", "latitud", "longitud",
    "fecha_acontecimiento", "fecha_carga", "ultimaModificacion",
    "origen", "estado", "contribuyente", "etiquetas",
    "contenidoMultimedia", "transicionesDeEstado"
})
@Data
public class HechoOutputDTO {

  @JsonProperty("id")
  private Long id;

  @JsonProperty("titulo")
  private String titulo;

  @JsonProperty("descripcion")
  private String descripcion;

  @JsonProperty("categoria")
  private String categoria;

  @JsonProperty("latitud")
  private Double latitud;

  @JsonProperty("longitud")
  private Double longitud;

  @JsonProperty("fecha_acontecimiento")
  private LocalDate fechaDeAcontecimiento;

  @JsonProperty("fecha_carga")
  private LocalDateTime fechaDeCarga;

  @JsonProperty("ultimaModificacion")
  private LocalDateTime ultimaModificacion;

  @JsonProperty("origen")
  private String origen;

  @JsonProperty("estado")
  private String estado;

  @JsonProperty("contribuyente")
  private Long contribuyente;

  @JsonProperty("etiquetas")
  private List<Long> etiquetas;

  @JsonProperty("contenidoMultimedia")
  private List<ContenidoMultimediaOutputDTO> contenidoMultimedia;

  @JsonProperty("transicionesDeEstado")
  private List<String> transicionesDeEstado;

  public HechoOutputDTO(
      Long id,
      String titulo,
      String descripcion,
      String categoria,
      Double latitud,
      Double longitud,
      LocalDate fechaDeAcontecimiento,
      LocalDateTime fechaDeCarga,
      LocalDateTime ultimaModificacion,
      Long contribuyente,
      List<ContenidoMultimediaOutputDTO> contenidoMultimedia
  ) {
    this.id = id;
    this.origen = "CONTRIBUYENTE";
    this.estado = "VISIBLE";
    this.contribuyente = contribuyente;
    this.etiquetas = new ArrayList<>();
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.categoria = categoria;
    this.latitud = latitud;
    this.longitud = longitud;
    this.fechaDeAcontecimiento = fechaDeAcontecimiento;
    this.fechaDeCarga = fechaDeCarga;
    this.ultimaModificacion = ultimaModificacion;
    this.contenidoMultimedia = contenidoMultimedia != null ? contenidoMultimedia : new ArrayList<>();
    //this.transicionesDeEstado = new ArrayList<>();
  }
}