package ar.utn.ba.ddsi.fuenteproxy.domain.request.validacion;

import ar.utn.ba.ddsi.fuenteproxy.domain.request.SolicitudDeEliminacion;

public interface Validacion {
  Boolean cumple(SolicitudDeEliminacion solicitud);
}