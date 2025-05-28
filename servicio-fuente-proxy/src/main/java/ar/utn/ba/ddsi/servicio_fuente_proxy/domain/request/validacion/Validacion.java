package ar.utn.ba.ddsi.servicio_fuente_proxy.domain.request.validacion;

import ar.utn.ba.ddsi.servicio_fuente_proxy.domain.request.SolicitudDeEliminacion;

public interface Validacion {
  Boolean cumple(SolicitudDeEliminacion solicitud);
}