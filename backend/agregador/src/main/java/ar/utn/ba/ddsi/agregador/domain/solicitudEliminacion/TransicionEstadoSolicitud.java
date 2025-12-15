package ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import org.hibernate.annotations.CreationTimestamp;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "transicion_estado_solicitud")
public class TransicionEstadoSolicitud {
  @Id
  @GeneratedValue(strategy= GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name="estado", nullable = false)
  private EstadoSolicitud estadoFinal;

  @Column(name = "fecha_transicion", nullable = false, updatable = false)
  @CreationTimestamp
  private LocalDate fecha;

  public TransicionEstadoSolicitud(EstadoSolicitud estadoSolicitud) {
    this.estadoFinal = estadoSolicitud;
  }
}
