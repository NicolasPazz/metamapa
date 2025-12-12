package ar.utn.ba.ddsi.agregador.domain.coleccion;

import ar.utn.ba.ddsi.agregador.repositorie.converter.AlgoritmoAttributeConverter;
import ar.utn.ba.ddsi.agregador.repositorie.converter.CriterioAttributeConverter;
import ar.utn.ba.ddsi.agregador.domain.coleccion.algoritmosConsenso.AAlgoritmoConsenso;
import ar.utn.ba.ddsi.agregador.domain.coleccion.criterios.CriterioCombinado;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.domain.coleccion.criterios.Criterio;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "coleccion")
public class Coleccion {
  @Id
  @GeneratedValue
  @Column(unique = true, updatable = false, nullable = false)
  private UUID handle;

  @Column(name = "titulo", nullable = false)
  private String titulo;

  @Column(name = "descripcion", columnDefinition = "TEXT", nullable = false)
  private String descripcion;

  @ManyToMany
  @JoinTable(
      name = "coleccion_hechos",
      joinColumns = @JoinColumn(name = "coleccion_handle", referencedColumnName = "handle"),
      inverseJoinColumns = @JoinColumn(name = "hecho_id", referencedColumnName = "id")
  )
  private List<Hecho> hechos;

  @Convert(converter = CriterioAttributeConverter.class)
  @Builder.Default
  private Criterio criterioDePertenencia = null;

  @Convert(converter = AlgoritmoAttributeConverter.class)
  @Builder.Default
  private AAlgoritmoConsenso algoritmoConsenso = null;

  @ManyToMany
  @JoinTable(
      name = "coleccion_hechos_consensuados",
      joinColumns = @JoinColumn(name = "coleccion_handle", referencedColumnName = "handle"),
      inverseJoinColumns = @JoinColumn(name = "hecho_id", referencedColumnName = "id")
  )
  private List<Hecho> hechosConsensuados;

  @Column(name = "fecha_carga", nullable = false, updatable = false)
  @CreationTimestamp
  private LocalDateTime fechaDeCarga;

  @Column(name = "fuente_id", nullable = false)
  private Long fuenteId;

  @Column(name = "id_origen", nullable = false)
  private UUID idOrigen;

  public void actualizarHechos() {
    if (this.criterioDePertenencia != null && this.hechos != null) {
      this.hechos = this.hechos.parallelStream()
          .filter(criterioDePertenencia::cumple)
          .filter(Hecho::esVisible)
          .toList();
    } else {
      this.hechos = List.of();
    }
  }

  public void actualizarHechosConsensuados(int fuentesTotales) {
    if (this.algoritmoConsenso != null && this.hechos != null) {
      this.hechosConsensuados = this.hechos.stream()
          .filter(hecho -> algoritmoConsenso.esConsensuado(hecho, fuentesTotales, this.hechos))
          .filter(Hecho::esVisible)
          .toList();
    } else {
      this.hechosConsensuados = this.hechos != null ? List.copyOf(this.hechos) : List.of();
    }
  }

  public void agregarCriterio(Criterio nuevo) {
    if (nuevo == null) return;

    if (criterioDePertenencia == null) {
      criterioDePertenencia = nuevo;
    } else if (criterioDePertenencia instanceof CriterioCombinado combinado) {
      combinado.agregarCriterio(nuevo);
    } else {
      CriterioCombinado combinado = new CriterioCombinado();
      combinado.agregarCriterio(criterioDePertenencia);
      combinado.agregarCriterio(nuevo);
      criterioDePertenencia = combinado;
    }
  }

  public void eliminarCriterio(Criterio aEliminar) {
    if (aEliminar == null || criterioDePertenencia == null) return;

    if (criterioDePertenencia instanceof CriterioCombinado combinado) {
      combinado.eliminarCriterio(aEliminar);
      List<Criterio> lista = combinado.getCriterios();
      if (lista.size() == 1) {
        criterioDePertenencia = lista.get(0);
      } else if (lista.isEmpty()) {
        criterioDePertenencia = null;
      }
    } else {
      if (criterioDePertenencia.equals(aEliminar)) {
        criterioDePertenencia = null;
      }
    }
  }
}
