package ar.utn.ba.ddsi.agregador.domain.hecho;

import ar.utn.ba.ddsi.agregador.domain.dto.external.TransicionEstadoHechoDTO;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

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
