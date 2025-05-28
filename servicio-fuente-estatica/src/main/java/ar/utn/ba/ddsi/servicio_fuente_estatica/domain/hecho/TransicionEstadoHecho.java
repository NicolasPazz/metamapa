package ar.utn.ba.ddsi.servicio_fuente_estatica.domain.hecho;

import ar.utn.ba.ddsi.servicio_fuente_estatica.domain.hecho.EstadoHecho;
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
