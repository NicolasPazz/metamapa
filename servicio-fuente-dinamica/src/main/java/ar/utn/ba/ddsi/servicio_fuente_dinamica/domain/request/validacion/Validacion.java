package ar.utn.ba.ddsi.servicio_fuente_dinamica.domain.request.validacion;

import ar.utn.ba.ddsi.servicio_fuente_dinamica.domain.request.SolicitudDeEliminacion;

public interface Validacion {
  Boolean cumple(SolicitudDeEliminacion solicitud);
}