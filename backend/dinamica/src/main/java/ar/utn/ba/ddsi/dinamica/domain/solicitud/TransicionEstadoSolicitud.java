package ar.utn.ba.ddsi.dinamica.domain.solicitud;

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

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "transicion_estado_solicitud")
public class TransicionEstadoSolicitud {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "estado", nullable = false)
  private EstadoSolicitud estado;

  @Column(name = "fecha_transicion", nullable = false, updatable = false)
  private LocalDateTime fecha;

  public TransicionEstadoSolicitud(EstadoSolicitud estado, LocalDateTime fecha) {
    this.estado = estado;
    this.fecha = fecha;
  }
}
