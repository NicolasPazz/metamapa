package ar.utn.ba.ddsi.dinamica.domain.hecho;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hecho")
public class Hecho {
  @Id
  @GeneratedValue(strategy= GenerationType.IDENTITY)
  private Long id;
  @Column(name = "id_solicitud")
  private Long idSolicitud;
  @Embedded
  private DataHecho data;
  @Enumerated(EnumType.ORDINAL)
  private OrigenHecho origen;
  @Enumerated(EnumType.ORDINAL)
  private EstadoHecho estado;
  @Column(name="contribuyente_id", nullable = true)
  private Long idContribuyente;  // private Contribuyente contribuyente;
  @ElementCollection
  @CollectionTable(
          name = "hecho_etiquetas",
          joinColumns = @JoinColumn(name = "hecho_id")
  )
  @Column(name = "etiquetas")
  @Builder.Default
  private Set<String> etiquetas = new HashSet<>();
  @OneToMany
  @JoinColumn(name = "multimedia_id")
  @Builder.Default
  private List<ContenidoMultimedia> multimediaPaths = new ArrayList<>();
  @ElementCollection
  @Builder.Default
  private List<TransicionEstadoHecho> transiciones = new ArrayList<>();
  @Column(name = "ultimaActualizacion")
  private LocalDateTime ultimaActualizacion;


  public void transicionarEstado(EstadoHecho estado) {
    this.transiciones.add(new TransicionEstadoHecho(estado, LocalDateTime.now()));
    this.estado = estado;
  }

  public String getTitulo() {
    return data.getTitulo();
  }

  public String getDescripcion() {
    return data.getDescripcion();
  }

  public Double getLatitud() {
    return data.getLatitud();
  }

  public Double getLongitud() {
    return data.getLongitud();
  }

  public LocalDate getFechaDeAcontecimiento() {
    return data.getFechaDeAcontecimiento();
  }

  public LocalDateTime getFechaDeCarga() {
    return data.getFechaDeCarga();
  }

  public String getNombreCategoria() {
    return data.getNombreCategoria();
  }

  /* public List<String> getMultimediaPaths() {
    return this.multimediaPaths.stream()
      .map(ContenidoMultimedia::getPathArchivo)
      .toList();
  } */
}