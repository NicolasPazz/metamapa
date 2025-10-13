package ar.utn.ba.ddsi.dinamica.domain.solicitud;

import ar.utn.ba.ddsi.dinamica.domain.dto.input.ContenidoMultimediaInputDTO;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SolicitudHecho {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long idSolicitud;
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
  @OneToMany
  @JoinColumn(name = "multimedia_id")
  private List<ContenidoMultimediaInputDTO> multimedia;
  @Column(name = "fechaDeCarga")
  private LocalDateTime fechaDeCargaSolicitud;
  @Enumerated(EnumType.ORDINAL)
  private EstadoSolicitud estado; //pendiente-aceptado-rechazado
  @Column(name = "sugerencia")
  private String sugerencia;
  @ElementCollection
  private List<TransicionEstadoSolicitud> transiciones;

}
