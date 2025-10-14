package ar.utn.ba.ddsi.dinamica.domain.hecho;

import ar.utn.ba.ddsi.dinamica.domain.dto.output.ContenidoMultimediaOutputDTO;
import ar.utn.ba.ddsi.dinamica.domain.solicitud.SolicitudHecho;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ContenidoMultimedia {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "nombre")
  private String pathArchivo;

  @Enumerated(EnumType.ORDINAL)
  private TipoContenido tipoContenido;

  @ManyToOne
  @JoinColumn(name = "solicitud_id")
  private SolicitudHecho solicitudHecho;

  public ContenidoMultimedia(String pathArchivo, TipoContenido tipoContenido, SolicitudHecho solicitud) {
    this.pathArchivo = pathArchivo;
    this.tipoContenido = tipoContenido;
    this.solicitudHecho = solicitud;
  }

  public ContenidoMultimediaOutputDTO toOutputDTO() {
    return new ContenidoMultimediaOutputDTO(this.pathArchivo, this.tipoContenido.name());
  }
}