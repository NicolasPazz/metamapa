package ar.utn.ba.ddsi.estadistica.domain.estadistica;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estadistica_categoria_dominante")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticaCategoriaDominante {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String categoriaNombre;

  @Column(nullable = false)
  private Long cantidadHechos;
}
