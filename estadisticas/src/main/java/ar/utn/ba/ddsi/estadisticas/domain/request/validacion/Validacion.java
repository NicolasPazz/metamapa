package ar.utn.ba.ddsi.estadisticas.domain.request.validacion;

import ar.utn.ba.ddsi.estadisticas.domain.request.SolicitudDeEliminacion;

public interface Validacion {
  Boolean cumple(SolicitudDeEliminacion solicitud);
}