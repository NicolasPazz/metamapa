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
import java.time.LocalDateTime;
import java.util.stream.Collectors;
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

  @Column(name = "fecha_carga", nullable = false)
  private LocalDateTime fechaDeCarga;

  @Enumerated(EnumType.STRING)
  @Column(name="origen", nullable = false)
  private OrigenHecho origen;


  @Column(name = "enviado", nullable = false)
  @Builder.Default
  private Boolean enviado = false;

  @OneToOne
  @JoinColumn(name = "solicitud_id", referencedColumnName = "id", nullable = false)
  private SolicitudHecho solicitud;

  public HechoOutputDTO toOutputDTO() {
    return new HechoOutputDTO(
        this.id,
        this.solicitud.getTitulo(),
        this.solicitud.getDescripcion(),
        this.solicitud.getCategoria(),
        this.solicitud.getLatitud(),
        this.solicitud.getLongitud(),
        this.solicitud.getFechaDeAcontecimiento(),
        this.fechaDeCarga,
        this.origen,
        this.solicitud.getIdContribuyente(),
        this.solicitud.getMultimedia().stream()
            .map(ContenidoMultimedia::toOutputDTO)
            .collect(Collectors.toList())
    );
  }
}