package ar.utn.ba.ddsi.agregador.domain.hecho;

import ar.utn.ba.ddsi.agregador.domain.hecho.multimedia.ContenidoMultimedia;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "hecho")
public class Hecho {
  @Id
  @GeneratedValue(strategy= GenerationType.IDENTITY)
  private Long id;

  @Column(name = "titulo", nullable = false)
  private String titulo;

  @Column(name = "descripcion", columnDefinition = "TEXT", nullable = false)
  private String descripcion;

  @Column(name = "latitud", nullable = false)
  private Double latitud;

  @Column(name = "longitud", nullable = false)
  private Double longitud;

  @Column(name = "fecha_de_acontecimiento", nullable = false)
  private LocalDate fechaDeAcontecimiento;

  @Column(name = "fecha_de_carga", nullable = false, updatable = false)
  @CreationTimestamp
  private LocalDateTime fechaDeCarga;

  @Enumerated(EnumType.STRING)
  @Column(name="origen", nullable = false)
  private OrigenHecho origen;

  @Enumerated(EnumType.STRING)
  @Column(name="estado", nullable = false)
  @Builder.Default
  private EstadoHecho estado = EstadoHecho.VISIBLE;

  @Column(name = "contribuyente_id", nullable = true)
  @Builder.Default
  private Long contribuyenteId = null;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "hecho_id", referencedColumnName = "id")
  @Builder.Default
  private Set<Etiqueta> etiquetas = new HashSet<>();

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "hecho_id", referencedColumnName = "id")
  @Builder.Default
  private List<ContenidoMultimedia> multimedia = new ArrayList<>();

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "hecho_id", referencedColumnName = "id")
  @Builder.Default
  private List<TransicionEstadoHecho> transiciones = new ArrayList<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "categoria_id", referencedColumnName = "id")
  private Categoria categoria;

  @Column(name = "fuente_id", nullable = false)
  private Long fuenteId;

  @Column(name = "id_origen", nullable = false)
  private Long idOrigen;

  public Hecho(String titulo, String descripcion, Double latitud, Double longitud, LocalDate fechaDeAcontecimiento,
               OrigenHecho origen, Long contribuyenteId, Categoria categoria, Long fuenteId) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.latitud = latitud;
    this.longitud = longitud;
    this.fechaDeAcontecimiento = fechaDeAcontecimiento;
    this.origen = origen;
    this.contribuyenteId = contribuyenteId;
    this.categoria = categoria;
    this.fuenteId = fuenteId;
    this.transicionarEstado(EstadoHecho.VISIBLE);
  }

  private void transicionarEstado(EstadoHecho estado) {
    this.transiciones.add(new TransicionEstadoHecho(estado));
    this.estado = estado;
  }

  public void ocultar() {
    this.transicionarEstado(EstadoHecho.OCULTO);
  }

  public void visibilizar() {
    this.transicionarEstado(EstadoHecho.VISIBLE);
  }

  public boolean esVisible() {
    return this.estado == EstadoHecho.VISIBLE;
  }

  public boolean contieneEtiqueta(String etiquetaBuscada) {
    if (etiquetaBuscada == null || etiquetaBuscada.isBlank()) return false;

    return this.etiquetas.stream()
        .map(Etiqueta::getNombre)
        .filter(Objects::nonNull)
        .map(String::toLowerCase)
        .anyMatch(nombre -> nombre.equals(etiquetaBuscada.trim().toLowerCase()));
  }
}