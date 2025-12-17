package ar.utn.ba.ddsi.estadistica.dto.input;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HechoDTO {
  private Long id;

  @JsonProperty("fecha_acontecimiento")
  private LocalDateTime fechaAcontecimiento;

  @JsonProperty("fecha_carga")
  private LocalDateTime fechaCarga;

  private Double latitud;
  private Double longitud;
  private String categoria;
  private String titulo;
  private String descripcion;
  private String origen;
}

