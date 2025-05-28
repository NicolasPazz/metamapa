package ar.utn.ba.ddsi.servicio_fuente_estatica.domain.request.validacion;

import ar.utn.ba.ddsi.servicio_fuente_estatica.domain.request.SolicitudDeEliminacion;

public interface Validacion {
  Boolean cumple(SolicitudDeEliminacion solicitud);
}