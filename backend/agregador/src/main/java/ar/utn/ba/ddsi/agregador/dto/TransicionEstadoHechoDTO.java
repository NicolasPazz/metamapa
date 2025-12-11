package ar.utn.ba.ddsi.agregador.dto;

import ar.utn.ba.ddsi.agregador.domain.hecho.EstadoHecho;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class TransicionEstadoHechoDTO {
  @JsonProperty("estado")
  private EstadoHecho estadoFinal;
  private LocalDateTime fecha;
}
