package ar.utn.ba.ddsi.fuenteproxy.domain.hecho;

import ar.utn.ba.ddsi.fuenteproxy.domain.hecho.EstadoHecho;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class TransicionEstadoHecho {
  private EstadoHecho estadoFinal;
  private LocalDate fechaTransicion;
}
