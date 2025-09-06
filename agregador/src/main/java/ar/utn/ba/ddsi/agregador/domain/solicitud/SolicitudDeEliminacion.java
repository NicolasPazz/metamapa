package ar.utn.ba.ddsi.agregador.domain.solicitud;

import ar.utn.ba.ddsi.agregador.domain.dto.output.SolicitudOutputDTO;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.domain.solicitud.validacion.Validacion;
import ar.utn.ba.ddsi.agregador.domain.solicitud.validacion.ValidacionLongitud;
import ar.utn.ba.ddsi.agregador.domain.solicitud.validacion.detector.DetectorDeSpamTFIDF;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SolicitudDeEliminacion {
  private Long id;
  private Hecho hecho;
  private String textoFundamentos;
  private LocalDate fecha;
  private EstadoSolicitud estado;
  private Long contribuyente;
  private List<TransicionEstadoSolicitud> transicionesDeEstado = new ArrayList<>();

  public SolicitudDeEliminacion(Hecho hecho, String textoFundamentos, Long contribuyente) {
      this.hecho = hecho;
      this.textoFundamentos = textoFundamentos;
      this.fecha = LocalDate.now();
      this.estado = EstadoSolicitud.PENDIENTE;
      this.contribuyente = contribuyente;
      this.transicionesDeEstado.add(new TransicionEstadoSolicitud(EstadoSolicitud.PENDIENTE));
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
    dto.setIdHecho(this.hecho != null ? this.hecho.getId() : null);
    dto.setTextoFundamentos(this.textoFundamentos);
    dto.setFecha(this.fecha);
    dto.setEstado(this.estado);
    dto.setIdContribuyente(this.contribuyente);
    return dto;
  }
}
