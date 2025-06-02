package ar.utn.ba.ddsi.proxy_metamapa.domain.request.validacion;

import ar.utn.ba.ddsi.proxy_metamapa.domain.request.SolicitudDeEliminacion;

public interface Validacion {
  Boolean cumple(SolicitudDeEliminacion solicitud);
}