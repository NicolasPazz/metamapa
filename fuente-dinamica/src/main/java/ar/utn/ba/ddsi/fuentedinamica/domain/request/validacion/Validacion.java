package ar.utn.ba.ddsi.fuentedinamica.domain.request.validacion;

import ar.utn.ba.ddsi.fuentedinamica.domain.request.SolicitudDeEliminacion;

public interface Validacion {
  Boolean cumple(SolicitudDeEliminacion solicitud);
}