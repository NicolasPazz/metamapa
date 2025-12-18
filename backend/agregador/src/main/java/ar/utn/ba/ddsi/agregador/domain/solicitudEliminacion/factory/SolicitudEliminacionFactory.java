package ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.factory;

import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.EstadoSolicitud;
import ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.SolicitudEliminacion;
import ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.exception.ValidacionException;
import ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.validacion.Validacion;
import ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.validacion.detector.DetectorDeSpam;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SolicitudEliminacionFactory {

  private final List<Validacion> validaciones;

  public SolicitudEliminacionFactory(List<Validacion> validaciones) {
    this.validaciones = validaciones;
    log.info("[SolicitudEliminacionFactory] Constructor llamado con {} validaciones",
        validaciones != null ? validaciones.size() : 0);
  }

  @PostConstruct
  public void init() {
    log.info("=================================================================");
    log.info("[SolicitudEliminacionFactory] Inicializando factory de solicitudes");
    log.info("[SolicitudEliminacionFactory] Validaciones registradas: {}",
        validaciones != null ? validaciones.size() : 0);

    if (validaciones != null) {
      for (int i = 0; i < validaciones.size(); i++) {
        Validacion v = validaciones.get(i);
        log.info("[SolicitudEliminacionFactory]   {} - {} (DetectorDeSpam: {})",
            i + 1, v.getClass().getName(), v instanceof DetectorDeSpam);
      }
    } else {
      log.error("[SolicitudEliminacionFactory] ¡NO HAY VALIDACIONES REGISTRADAS!");
    }
    log.info("=================================================================");
  }

  public SolicitudEliminacion crearSolicitud(Hecho hecho, String textoFundamentos, Long contribuyenteId,
                                             Long fuenteId, Long idOrigen) {
    log.info("[SolicitudEliminacionFactory] Iniciando creación de solicitud");
    log.info("[SolicitudEliminacionFactory] Texto fundamentos longitud: {}",
        textoFundamentos != null ? textoFundamentos.length() : 0);
    log.info("[SolicitudEliminacionFactory] Cantidad de validaciones registradas: {}",
        validaciones != null ? validaciones.size() : 0);

    // Log de las validaciones disponibles
    if (validaciones != null) {
      for (int i = 0; i < validaciones.size(); i++) {
        Validacion v = validaciones.get(i);
        log.info("[SolicitudEliminacionFactory] Validacion {}: {} (es DetectorDeSpam: {})",
            i + 1, v.getClass().getSimpleName(), v instanceof DetectorDeSpam);
      }
    }

    // PRIMERO: Validar validaciones NO-SPAM (longitud, formato, etc.)
    // Estas deben lanzar excepción si fallan
    if (validaciones != null) {
      for (Validacion v : validaciones) {
        if (!(v instanceof DetectorDeSpam)) {
          log.debug("[SolicitudEliminacionFactory] Ejecutando validación: {}", v.getClass().getSimpleName());
          if (!v.esValida(textoFundamentos)) {
            log.warn("[SolicitudEliminacionFactory] Validación {} falló: {}",
                v.getClass().getSimpleName(), v.mensajeError());
            throw new ValidacionException(v.mensajeError());
          }
          log.debug("[SolicitudEliminacionFactory] Validación {} pasó correctamente", v.getClass().getSimpleName());
        }
      }
    }

    // SEGUNDO: Verificar si es SPAM (ahora se PERSISTE con flag spam=true para estadísticas)
    boolean esSpam = false;
    if (validaciones != null) {
      for (Validacion v : validaciones) {
        if (v instanceof DetectorDeSpam) {
          log.debug("[SolicitudEliminacionFactory] Ejecutando detector de spam: {}", v.getClass().getSimpleName());
          if (!v.esValida(textoFundamentos)) {
            log.warn("[SolicitudEliminacionFactory] Texto detectado como SPAM por: {}", v.getClass().getSimpleName());
            log.info("[SolicitudEliminacionFactory] Solicitud será persistida con flag spam=true (para estadísticas)");
            esSpam = true;
            break; // No necesitamos verificar más detectores
          }
        }
      }
    }

    // Crear la solicitud (incluso si es spam, para fines estadísticos)
    SolicitudEliminacion solicitud = new SolicitudEliminacion(hecho, truncarTextoFundamentos(textoFundamentos),
        contribuyenteId, fuenteId, idOrigen);

    solicitud.setSpam(esSpam);

    if (esSpam) {
      // Solicitudes spam se rechazan automáticamente
      solicitud.transicionarEstado(EstadoSolicitud.RECHAZADA);
      log.info("[SolicitudEliminacionFactory] Solicitud SPAM creada - Estado: RECHAZADA (para estadísticas)");
    } else {
      solicitud.transicionarEstado(EstadoSolicitud.PENDIENTE);
      log.info("[SolicitudEliminacionFactory] Solicitud creada exitosamente - Estado: PENDIENTE");
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
