package ar.utn.ba.ddsi.agregador.dto;

import ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.EstadoSolicitud;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class TransicionEstadoSolicitudDTO {
  @JsonProperty("estado")
  private EstadoSolicitud estadoFinal;
  private LocalDateTime fecha;
}
