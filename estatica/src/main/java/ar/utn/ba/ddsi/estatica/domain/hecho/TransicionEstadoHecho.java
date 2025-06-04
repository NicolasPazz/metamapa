package ar.utn.ba.ddsi.estatica.domain.hecho;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
public class TransicionEstadoHecho {
  private EstadoHecho estadoFinal;
  private LocalDate fechaTransicion;
}
