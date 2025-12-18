package ar.utn.ba.ddsi.dinamica.domain.dto.output;

import ar.utn.ba.ddsi.dinamica.domain.solicitud.EstadoSolicitud;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransicionEstadoOutputDTO {
  @JsonProperty("estado")
  private EstadoSolicitud estado;
  private LocalDateTime fecha;
}

