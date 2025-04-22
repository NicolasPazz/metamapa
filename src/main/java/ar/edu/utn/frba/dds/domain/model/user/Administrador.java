package ar.edu.utn.frba.dds.domain.model.user;

import ar.edu.utn.frba.dds.domain.datasource.Fuente;
import ar.edu.utn.frba.dds.domain.model.coleccion.Coleccion;
import ar.edu.utn.frba.dds.domain.model.coleccion.criterioDePertenencia.Criterio;
import ar.edu.utn.frba.dds.domain.model.request.SolicitudDeEliminacion;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Administrador {
  public Coleccion crearColeccion(String titulo, String descripcion, Fuente fuente, Criterio criterioDePertenencia) {
    return new Coleccion(titulo, descripcion, fuente, criterioDePertenencia);
  }

  public void aceptarSolicitud(SolicitudDeEliminacion solicitud) {
    solicitud.aceptar();
  }

  public void rechazarSolicitud(SolicitudDeEliminacion solicitud) {
    solicitud.rechazar();
  }
}
