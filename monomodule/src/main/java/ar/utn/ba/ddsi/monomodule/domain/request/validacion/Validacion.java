package ar.utn.ba.ddsi.monomodule.domain.request.validacion;

import ar.utn.ba.ddsi.monomodule.domain.request.SolicitudDeEliminacion;

public interface Validacion {
  Boolean cumple(SolicitudDeEliminacion solicitud);
}