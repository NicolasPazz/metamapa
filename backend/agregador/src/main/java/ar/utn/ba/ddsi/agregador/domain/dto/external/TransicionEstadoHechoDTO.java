package ar.utn.ba.ddsi.agregador.domain.dto.external;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class TransicionEstadoHechoDTO {
  private String estadoFinal;
  private LocalDateTime fecha;
}
