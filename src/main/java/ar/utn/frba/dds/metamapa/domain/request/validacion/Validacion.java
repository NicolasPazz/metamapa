package ar.utn.frba.dds.metamapa.domain.request.validacion;

import ar.utn.frba.dds.metamapa.domain.request.SolicitudDeEliminacion;

public interface Validacion {
  Boolean cumple(SolicitudDeEliminacion solicitud);
}