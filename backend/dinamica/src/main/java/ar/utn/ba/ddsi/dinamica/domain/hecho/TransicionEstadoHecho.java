package ar.utn.ba.ddsi.dinamica.domain.hecho;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class TransicionEstadoHecho {
  private EstadoHecho estadoFinal;
  private LocalDateTime fechaTransicion;
}
