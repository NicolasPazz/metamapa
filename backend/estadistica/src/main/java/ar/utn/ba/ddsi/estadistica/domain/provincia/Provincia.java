package ar.utn.ba.ddsi.estadistica.domain.provincia;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "provincias")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Provincia {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String nombre;

  @Column(nullable = false)
  private Double latitudCentro;

  @Column(nullable = false)
  private Double longitudCentro;

  @Column(nullable = false)
  private Double radioKm;
}