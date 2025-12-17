package ar.utn.ba.ddsi.estadistica.domain.estadistica;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estadistica_coleccion_provincia")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticaColeccionProvincia {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String coleccionHandle;

  @Column(nullable = false)
  private String coleccionTitulo;

  @Column(nullable = false)
  private String provinciaNombre;

  @Column(nullable = false)
  private Long cantidadHechos;
}

