package ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.factory;

import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.EstadoSolicitud;
import ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.SolicitudEliminacion;
import ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.exception.ValidacionException;
import ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.validacion.Validacion;
import ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.validacion.detector.DetectorDeSpam;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SolicitudEliminacionFactory {

  private final List<Validacion> validaciones;

  public SolicitudEliminacionFactory(List<Validacion> validaciones) {
    this.validaciones = validaciones;
  }

  public SolicitudEliminacion crearSolicitud(Hecho hecho, String textoFundamentos, Long contribuyenteId) {
    SolicitudEliminacion solicitud = new SolicitudEliminacion(hecho, textoFundamentos, contribuyenteId);

    boolean esSpam = false;

    for (Validacion v : validaciones) {
      if (!v.esValida(textoFundamentos)) {
        if (v instanceof DetectorDeSpam) {
          esSpam = true;
          solicitud.transicionarEstado(EstadoSolicitud.RECHAZADA);
        } else {
          throw new ValidacionException(v.mensajeError());
        }
      }
    }
    solicitud.setSpam(esSpam);

    if (!esSpam) {
      solicitud.transicionarEstado(EstadoSolicitud.PENDIENTE);
    }

    return solicitud;
  }
}
