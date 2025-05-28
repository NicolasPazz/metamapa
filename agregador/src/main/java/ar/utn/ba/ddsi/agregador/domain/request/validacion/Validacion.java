package ar.utn.ba.ddsi.agregador.domain.request.validacion;

import ar.utn.ba.ddsi.agregador.domain.request.SolicitudDeEliminacion;

public interface Validacion {
  Boolean cumple(SolicitudDeEliminacion solicitud);
}