package ar.utn.ba.ddsi.agregador.domain.dto;

import ar.utn.ba.ddsi.agregador.domain.hecho.EstadoHecho;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class TransicionEstadoHechoDTO {
  private EstadoHecho estadoFinal;
  private LocalDateTime fecha;
}
