package ar.utn.ba.ddsi.dinamica.domain.hecho;

import ar.utn.ba.ddsi.dinamica.domain.dto.output.HechoOutputDTO;
import ar.utn.ba.ddsi.dinamica.domain.solicitud.SolicitudHecho;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hecho")
public class Hecho {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "titulo")
  private String titulo;

  @Column(name = "descripcion")
  private String descripcion;

  @Column(name = "categoria")
  private String nombreCategoria;

  @Column(name = "latitud")
  private Double latitud;

  @Column(name = "longitud")
  private Double longitud;

  @Column(name = "fecha_de_acontecimiento")
  private LocalDate fechaDeAcontecimiento;

  @Column(name = "fecha_de_carga")
  @Builder.Default
  private LocalDateTime fechaDeCarga = LocalDateTime.now();

  @Enumerated(EnumType.ORDINAL)
  private OrigenHecho origen;

  @Enumerated(EnumType.ORDINAL)
  private EstadoHecho estado;

  @Column(name = "contribuyente_id", nullable = true)
  private Long idContribuyente;

  private Boolean enviado;

  @OneToOne
  @JoinColumn(name = "solicitud_id")
  private SolicitudHecho solicitud;

  @Transient
  @Builder.Default
  private List<ContenidoMultimedia> multimedia = new ArrayList<>();

  /*@ElementCollection
  @Builder.Default
  private List<TransicionEstadoHecho> transiciones = new ArrayList<>();


  @Column(name = "ultimaModificacion")
  private LocalDateTime ultimaModificacion;

  public void transicionarEstado(EstadoHecho estado) {
    this.transiciones.add(new TransicionEstadoHecho(estado, LocalDateTime.now()));
    this.estado = estado;
  }*/

  public HechoOutputDTO toOutputDTO() {
    return new HechoOutputDTO(
        this.id,
        this.titulo,
        this.descripcion,
        this.nombreCategoria,
        this.latitud,
        this.longitud,
        this.fechaDeAcontecimiento,
        this.fechaDeCarga,
        this.fechaDeCarga,
        this.idContribuyente,
        this.multimedia.stream()
            .map(ContenidoMultimedia::toOutputDTO)
            .toList()
    );
  }
}