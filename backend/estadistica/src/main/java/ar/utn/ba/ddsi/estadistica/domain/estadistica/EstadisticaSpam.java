package ar.utn.ba.ddsi.estadistica.domain.estadistica;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estadistica_spam")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticaSpam {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long totalSolicitudes;

  @Column(nullable = false)
  private Long solicitudesSpam;

  @Column(nullable = false)
  private Double porcentajeSpam;
}

