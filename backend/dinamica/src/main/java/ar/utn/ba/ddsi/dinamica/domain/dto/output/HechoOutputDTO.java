package ar.utn.ba.ddsi.dinamica.domain.dto.output;

import ar.utn.ba.ddsi.dinamica.domain.hecho.OrigenHecho;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

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
  private LocalDateTime fechaDeAcontecimiento;

  @JsonProperty("fecha_carga")
  private LocalDateTime fechaDeCarga;

  @JsonProperty("origen")
  private OrigenHecho origen;

  @JsonProperty("contribuyente_id")
  private Long idContribuyente;

  @JsonProperty("multimedia")
  private List<ContenidoMultimediaOutputDTO> contenidoMultimedia;

  public HechoOutputDTO(
      Long id,
      String titulo,
      String descripcion,
      String categoria,
      Double latitud,
      Double longitud,
      LocalDateTime fechaDeAcontecimiento,
      LocalDateTime fechaDeCarga,
      OrigenHecho origen,
      Long idContribuyente,
      List<ContenidoMultimediaOutputDTO> contenidoMultimedia
  ) {
    this.id = id;
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.categoria = categoria;
    this.latitud = latitud;
    this.longitud = longitud;
    this.fechaDeAcontecimiento = fechaDeAcontecimiento;
    this.fechaDeCarga = fechaDeCarga;
    this.origen = origen;
    this.idContribuyente = idContribuyente;
    this.contenidoMultimedia = contenidoMultimedia != null ? contenidoMultimedia : new ArrayList<>();
  }
}

