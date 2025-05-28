package ar.utn.ba.ddsi.servicio_agregador.domain.request.validacion;

import ar.utn.ba.ddsi.servicio_agregador.domain.request.SolicitudDeEliminacion;

public interface Validacion {
  Boolean cumple(SolicitudDeEliminacion solicitud);
}