package ar.utn.ba.ddsi.agregador.dto;

import ar.utn.ba.ddsi.agregador.domain.hecho.EstadoHecho;
import ar.utn.ba.ddsi.agregador.domain.hecho.OrigenHecho;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class HechoDTO {
  private Long id;
  private String titulo;
  private String descripcion;

  @DecimalMin(value = "-90.0", message = "La latitud debe estar entre -90 y 90 grados")
  @DecimalMax(value = "90.0", message = "La latitud debe estar entre -90 y 90 grados")
  private Double latitud;

  @DecimalMin(value = "-180.0", message = "La longitud debe estar entre -180 y 180 grados")
  @DecimalMax(value = "180.0", message = "La longitud debe estar entre -180 y 180 grados")
  private Double longitud;
  @JsonProperty("fecha_acontecimiento")
  private LocalDate fechaDeAcontecimiento;
  @JsonProperty("fecha_carga")
  private LocalDateTime fechaDeCarga;
  private OrigenHecho origen;
  private EstadoHecho estado;
  @JsonProperty("contribuyente_id")
  private Long contribuyenteId;
  private List<String> etiquetas;
  private List<ContenidoMultimediaDTO> multimedia;
  private List<TransicionEstadoHechoDTO> transiciones;
  private String categoria;
  @JsonProperty("fuente_id")
  private Long fuenteId;
  @JsonProperty("id_origen")
  private Long idOrigen;
}