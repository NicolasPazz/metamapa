package ar.edu.utn.frba.dds.domain.request.validacion;

import ar.edu.utn.frba.dds.domain.request.SolicitudDeEliminacion;

public interface Validacion {
  Boolean cumple(SolicitudDeEliminacion solicitud);
}