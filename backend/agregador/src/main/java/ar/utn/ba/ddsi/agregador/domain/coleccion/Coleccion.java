package ar.utn.ba.ddsi.agregador.domain.coleccion;

import ar.utn.ba.ddsi.agregador.repositorie.converter.AlgoritmoAttributeConverter;
import ar.utn.ba.ddsi.agregador.repositorie.converter.CriterioListAttributeConverter;
import ar.utn.ba.ddsi.agregador.domain.coleccion.algoritmosConsenso.AAlgoritmoConsenso;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.domain.coleccion.criterios.Criterio;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

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
  @UuidGenerator
  @JdbcTypeCode(SqlTypes.VARCHAR)
  @Column(length = 36, updatable = false, nullable = false)
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

  @Convert(converter = CriterioListAttributeConverter.class)
  @Column(name = "criterios_pertenencia", columnDefinition = "TEXT")
  @Builder.Default
  private List<Criterio> criteriosDePertenencia = new ArrayList<>();

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


  public void actualizarHechos() {
    if (this.criteriosDePertenencia != null && !this.criteriosDePertenencia.isEmpty() && this.hechos != null) {
      this.hechos = this.hechos.stream()
          .filter(hecho -> criteriosDePertenencia.stream().allMatch(criterio -> criterio.cumple(hecho)))
          .filter(Hecho::esVisible)
          .toList();
    } else if (this.hechos != null) {
      this.hechos = this.hechos.stream()
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
    if (this.criteriosDePertenencia == null) {
      this.criteriosDePertenencia = new ArrayList<>();
    }
    this.criteriosDePertenencia.add(nuevo);
  }

  public void eliminarCriterio(Criterio aEliminar) {
    if (aEliminar == null || this.criteriosDePertenencia == null) return;
    this.criteriosDePertenencia.remove(aEliminar);
  }
}
