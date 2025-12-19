package ar.utn.ba.ddsi.frontend.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransicionEstadoHechoDTO {
  @JsonProperty("estado")
  private String estadoFinal;
  private LocalDateTime fecha;
}
