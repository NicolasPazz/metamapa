package ar.utn.ba.ddsi.proxy_metamapa.domain.dto;

import ar.utn.ba.ddsi.proxy_metamapa.domain.enums.EstadoHecho;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class TransicionEstadoHechoDTO {
  private EstadoHecho estadoFinal;
  private LocalDateTime fecha;

  public TransicionEstadoHechoDTO(EstadoHecho estadoFinal, LocalDateTime fecha) {
    this.estadoFinal = estadoFinal;
    this.fecha = fecha;
  }
}
