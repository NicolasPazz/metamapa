package ar.utn.ba.ddsi.dinamica.domain.solicitud;

import ar.utn.ba.ddsi.dinamica.domain.dto.output.SolicitudOutputDTO;
import ar.utn.ba.ddsi.dinamica.domain.hecho.ContenidoMultimedia;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class SolicitudHecho {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "id_contribuyente")
  private Long idContribuyente;

  @Column(name = "titulo")
  private String titulo;

  @Column(name = "descripcion")
  private String descripcion;

  @Column(name = "categoria")
  private String categoria;

  @Column(name = "latitud")
  private double latitud;

  @Column(name = "longitud")
  private double longitud;

  @Column(name = "fechaDeAcontecimiento")
  private LocalDate fechaDeAcontecimiento;

  @OneToMany(mappedBy = "solicitudHecho", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<ContenidoMultimedia> multimedia = new ArrayList<>();

  @Column(name = "fechaDeCarga")
  @Builder.Default
  private LocalDateTime fechaDeCargaSolicitud = LocalDateTime.now();

  @Enumerated(EnumType.ORDINAL)
  @Builder.Default
  private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE; //pendiente-aceptado-rechazado

  /*@ElementCollection
  private List<TransicionEstadoSolicitud> transiciones;*/

  public SolicitudOutputDTO toOutputDTO() {
    var dto = new SolicitudOutputDTO();
    dto.setId(this.getId());
    dto.setIdContribuyente(this.getIdContribuyente());
    dto.setTitulo(this.getTitulo());
    dto.setDescripcion(this.getDescripcion());
    dto.setNombreCategoria(this.getCategoria());
    dto.setLatitud(this.getLatitud());
    dto.setLongitud(this.getLongitud());
    dto.setFechaDeAcontecimiento(this.getFechaDeAcontecimiento().toString());
    if (!this.getMultimedia().isEmpty()) {
      dto.setContenidoMultimedia(this.getMultimedia().stream()
          .map(ContenidoMultimedia::toOutputDTO)
          .toList());
    }
    dto.setFechaDeCargaSolicitud(this.getFechaDeCargaSolicitud().toString());
    dto.setEstado(this.getEstado().toString());

    return dto;
  }
}
