package ar.utn.ba.ddsi.estadistica.domain.hecho;

import ar.utn.ba.ddsi.estadistica.domain.Coleccion;
import ar.utn.ba.ddsi.estadistica.domain.provincia.Provincia;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "coleccion_provincia")
public class HechosColeccionProvincia {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "coleccion_handle")
  private Coleccion coleccion;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "provincia_id")
  private Provincia provincia;

  @Column(name = "cantidad_hechos")
  private Long cantidadHechos;
}
