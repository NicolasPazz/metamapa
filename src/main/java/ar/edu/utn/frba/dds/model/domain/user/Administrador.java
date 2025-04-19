package ar.edu.utn.frba.dds.model.domain.user;

import ar.edu.utn.frba.dds.model.domain.coleccion.Coleccion;
import ar.edu.utn.frba.dds.model.domain.request.SolicitudDeEliminacion;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Administrador {
  public void crearColeccion(Coleccion coleccion) {
    // TODO
  }

  public void aceptarSolicitud(SolicitudDeEliminacion solicitud) {
    solicitud.aceptar();
  }

  public void rechazarSolicitud(SolicitudDeEliminacion solicitud) {
    solicitud.rechazar();
  }
}
