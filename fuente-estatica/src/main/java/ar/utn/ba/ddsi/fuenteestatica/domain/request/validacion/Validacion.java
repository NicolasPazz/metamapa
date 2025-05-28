package ar.utn.ba.ddsi.fuenteestatica.domain.request.validacion;

import ar.utn.ba.ddsi.fuenteestatica.domain.request.SolicitudDeEliminacion;

public interface Validacion {
  Boolean cumple(SolicitudDeEliminacion solicitud);
}