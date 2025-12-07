package ar.utn.ba.ddsi.agregador.domain.hecho;

import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class TransicionEstadoHecho {
  private Long id;
  private EstadoHecho estadoFinal;
  private LocalDateTime fechaTransicion;

  public TransicionEstadoHecho(EstadoHecho estado) {
    this.estadoFinal = estado;
    this.fechaTransicion = LocalDateTime.now();
  }

  public TransicionEstadoHecho(EstadoHecho estado, LocalDateTime fechaTransicion) {
    this.estadoFinal = estado;
    this.fechaTransicion = fechaTransicion;
  }
}
