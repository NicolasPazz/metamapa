package ar.utn.ba.ddsi.agregador.domain.hecho;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class TransicionEstadoHecho {
  private EstadoHecho estadoFinal;
  private LocalDateTime fechaTransicion;
}
