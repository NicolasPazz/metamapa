package ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.factory;

import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.EstadoSolicitud;
import ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.SolicitudEliminacion;
import ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.exception.ValidacionException;
import ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.validacion.Validacion;
import ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.validacion.detector.DetectorDeSpam;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SolicitudEliminacionFactory {

  private final List<Validacion> validaciones;

  public SolicitudEliminacionFactory(List<Validacion> validaciones) {
    this.validaciones = validaciones;
  }

  public SolicitudEliminacion crearSolicitud(Hecho hecho, String textoFundamentos, Long contribuyenteId,
                                             Long fuenteId, Long idOrigen) {
    SolicitudEliminacion solicitud = new SolicitudEliminacion(hecho, truncarTextoFundamentos(textoFundamentos),
        contribuyenteId, fuenteId, idOrigen);

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

  private String truncarTextoFundamentos(String texto) {
    if (texto != null && texto.length() > 2000) {
      log.warn("[SolicitudEliminacionFactory] Texto de fundamentos truncado por exceder 2000 caracteres");
      return texto.substring(0, 1997) + "...";
    }
    return texto;
  }
}
