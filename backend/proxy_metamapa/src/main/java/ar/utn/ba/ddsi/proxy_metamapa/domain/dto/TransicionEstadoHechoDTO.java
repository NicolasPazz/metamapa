package ar.utn.ba.ddsi.proxy_metamapa.domain.dto;

import ar.utn.ba.ddsi.proxy_metamapa.domain.enums.EstadoHecho;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransicionEstadoHechoDTO {
  @JsonProperty("estado")
  private EstadoHecho estadoFinal;
  private LocalDateTime fecha;
}
