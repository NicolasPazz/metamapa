package ar.utn.ba.ddsi.agregador.domain.coleccion;

import ar.utn.ba.ddsi.agregador.repositorie.converter.AlgoritmoAttributeConverter;
import ar.utn.ba.ddsi.agregador.repositorie.converter.CriterioListAttributeConverter;
import ar.utn.ba.ddsi.agregador.domain.coleccion.algoritmosConsenso.AAlgoritmoConsenso;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.domain.hecho.OrigenHecho;
import ar.utn.ba.ddsi.agregador.domain.coleccion.criterios.Criterio;
import ar.utn.ba.ddsi.agregador.domain.coleccion.criterios.CriterioOrigen;
import ar.utn.ba.ddsi.agregador.domain.coleccion.criterios.CriterioFuente;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
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

  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(
      name = "coleccion_hechos",
      joinColumns = @JoinColumn(name = "coleccion_handle", referencedColumnName = "handle"),
      inverseJoinColumns = @JoinColumn(name = "hecho_id", referencedColumnName = "id")
  )
  @Builder.Default
  private List<Hecho> hechos = new ArrayList<>();

  @Convert(converter = CriterioListAttributeConverter.class)
  @Column(name = "criterios_pertenencia", columnDefinition = "TEXT")
  @Builder.Default
  private List<Criterio> criteriosDePertenencia = new ArrayList<>();

  @Convert(converter = AlgoritmoAttributeConverter.class)
  @Builder.Default
  private AAlgoritmoConsenso algoritmoConsenso = null;

  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(
      name = "coleccion_hechos_consensuados",
      joinColumns = @JoinColumn(name = "coleccion_handle", referencedColumnName = "handle"),
      inverseJoinColumns = @JoinColumn(name = "hecho_id", referencedColumnName = "id")
  )
  @Builder.Default
  private List<Hecho> hechosConsensuados = new ArrayList<>();

  @Column(name = "fecha_carga", nullable = false, updatable = false)
  @CreationTimestamp
  private LocalDateTime fechaDeCarga;

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

  public List<Hecho> aplicarCriteriosDePertenencia(List<Hecho> hechos) {
    if (hechos == null || hechos.isEmpty()) {
      return new ArrayList<>();
    }
    if (this.criteriosDePertenencia == null || this.criteriosDePertenencia.isEmpty()) {
      return new ArrayList<>(hechos);
    }
    return hechos.stream()
        .filter(hecho -> this.criteriosDePertenencia.stream()
            .allMatch(criterio -> criterio.cumple(hecho)))
        .collect(Collectors.toList());
  }

  public List<Hecho> calcularHechosConsensuados(int cantidadFuentes) {
    if (this.hechos == null || this.hechos.isEmpty()) {
      return new ArrayList<>();
    }
    if (this.algoritmoConsenso == null) {
      return new ArrayList<>(this.hechos);
    }
    return this.hechos.stream()
        .filter(hecho -> this.algoritmoConsenso.esConsensuado(hecho, cantidadFuentes, this.hechos))
        .collect(Collectors.toList());
  }

  public void recalcularHechos(List<Hecho> hechosLocalesDisponibles, int cantidadFuentes) {
    List<Hecho> hechosPersistidos = hechosLocalesDisponibles.stream()
        .filter(h -> h.getId() != null)
        .toList();
    List<Hecho> hechosFiltrados = this.aplicarCriteriosDePertenencia(hechosPersistidos);
    if (this.hechos == null) {
      this.hechos = new ArrayList<>();
    }
    this.hechos.clear();
    this.hechos.addAll(hechosFiltrados);
    List<Hecho> consensuados = this.calcularHechosConsensuados(cantidadFuentes);
    if (this.hechosConsensuados == null) {
      this.hechosConsensuados = new ArrayList<>();
    }
    this.hechosConsensuados.clear();
    this.hechosConsensuados.addAll(consensuados);
  }

  public boolean tieneHechosExternos(List<Long> fuentesTiempoReal, List<OrigenHecho> origenesTiempoReal) {
    if (this.criteriosDePertenencia == null || this.criteriosDePertenencia.isEmpty()) {
      return false;
    }
    return this.criteriosDePertenencia.stream()
        .anyMatch(criterio -> {
          if (criterio instanceof CriterioOrigen criterioOrigen) {
            return origenesTiempoReal.contains(criterioOrigen.getOrigen());
          }
          if (criterio instanceof CriterioFuente criterioFuente) {
            return criterioFuente.getFuentes().stream()
                .anyMatch(fuentesTiempoReal::contains);
          }
          return false;
        });
  }

  public List<Hecho> aplicarAlgoritmoConsenso(List<Hecho> todosHechos, int cantidadFuentes) {
    if (this.algoritmoConsenso == null) {
      return new ArrayList<>(todosHechos);
    }
    return todosHechos.stream()
        .filter(hecho -> this.algoritmoConsenso.esConsensuado(hecho, cantidadFuentes, todosHechos))
        .collect(Collectors.toList());
  }
}
