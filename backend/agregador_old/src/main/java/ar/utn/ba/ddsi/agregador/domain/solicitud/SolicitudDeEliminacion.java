package ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion;

import ar.utn.ba.ddsi.agregador.domain.dto.output.SolicitudOutputDTO;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.validacion.detector.DetectorDeSpamTFIDF;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "solicitud_de_eliminacion")
public class SolicitudDeEliminacion {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "hecho_id", nullable = false)
  private Long hechoId;
  @Column(name = "textoFundamentos", nullable = false, length = 2000)
  private String textoFundamentos;
  @Column(name = "fecha", nullable = false)
  private LocalDate fecha;

  @Enumerated(EnumType.STRING)
  @Column(name="estado", nullable = false)
  private EstadoSolicitud estado;

  @Column(name = "contribuyenteId", nullable = true)
  private Long contribuyente;

  private Boolean spam;
  @ElementCollection
  private List<TransicionEstadoSolicitud> transicionesDeEstado = new ArrayList<>();

  public SolicitudDeEliminacion(Hecho hecho, String textoFundamentos, Long contribuyente) {
    this.hechoId = hecho != null ? hecho.getId() : null;
    this.textoFundamentos = textoFundamentos;
    this.fecha = LocalDate.now();
    this.estado = EstadoSolicitud.PENDIENTE;
    this.contribuyente = contribuyente;
    this.transicionesDeEstado.add(new TransicionEstadoSolicitud(EstadoSolicitud.PENDIENTE));
    this.spam = false; // Temporalmente, no detectar spam
  }

  public void aceptar() {
    transicionesDeEstado.add(new TransicionEstadoSolicitud(EstadoSolicitud.ACEPTADA));
    this.estado = EstadoSolicitud.ACEPTADA;
  }

  public void rechazar() {
    transicionesDeEstado.add(new TransicionEstadoSolicitud(EstadoSolicitud.RECHAZADA));
    this.estado = EstadoSolicitud.RECHAZADA;
  }

  public SolicitudOutputDTO toDTO() {
    SolicitudOutputDTO dto = new SolicitudOutputDTO();
    dto.setId(this.id);
    dto.setIdHecho(this.hechoId);
    dto.setTextoFundamentos(this.textoFundamentos);
    dto.setFecha(this.fecha);
    dto.setEstado(this.estado);
    dto.setIdContribuyente(this.contribuyente);
    dto.setSpam(this.spam);
    return dto;
  }
}
