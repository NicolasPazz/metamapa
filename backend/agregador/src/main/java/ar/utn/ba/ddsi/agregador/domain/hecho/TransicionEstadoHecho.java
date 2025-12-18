package ar.utn.ba.ddsi.agregador.domain.hecho;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "transicion_estado_hecho")
public class TransicionEstadoHecho {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "estado", nullable = false)
  private EstadoHecho estadoFinal;

  @Column(name = "fecha_transicion", nullable = false, updatable = false)
  @CreationTimestamp
  private LocalDateTime fechaTransicion;

  public TransicionEstadoHecho(EstadoHecho estado) {
    this.estadoFinal = estado;
  }
}
