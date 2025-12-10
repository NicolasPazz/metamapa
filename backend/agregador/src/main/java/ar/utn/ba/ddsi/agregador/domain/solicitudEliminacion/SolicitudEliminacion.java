package ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion;

import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "solicitud_eliminacion")
public class SolicitudEliminacion {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "hecho_id", referencedColumnName = "id", nullable = false)
  private Hecho hecho;

  @Column(name = "textoFundamentos", nullable = false, length = 5000)
  private String textoFundamentos;

  @Column(name = "fecha", nullable = false, updatable = false)
  @CreationTimestamp
  private LocalDate fecha;

  @Enumerated(EnumType.STRING)
  @Column(name="estado", nullable = false)
  private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

  @Column(name = "contribuyente_id", nullable = true)
  private Long contribuyenteId = null;

  @Column(name = "es_spam", nullable = false)
  private Boolean spam = false;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "solicitud_eliminacion_id", referencedColumnName = "id")
  private List<TransicionEstadoSolicitud> transiciones = new ArrayList<>();

  public SolicitudEliminacion(Hecho hecho, String textoFundamentos, Long contribuyenteId) {
    this.hecho = hecho;
    this.textoFundamentos = textoFundamentos;
    this.contribuyenteId = contribuyenteId;
  }

  public void transicionarEstado(EstadoSolicitud estado) {
    this.transiciones.add(new TransicionEstadoSolicitud(estado));
    this.estado = estado;
  }

  public void aceptar() {
    this.transicionarEstado(EstadoSolicitud.ACEPTADA);
  }

  public void rechazar() {
    this.transicionarEstado(EstadoSolicitud.RECHAZADA);
  }
}
