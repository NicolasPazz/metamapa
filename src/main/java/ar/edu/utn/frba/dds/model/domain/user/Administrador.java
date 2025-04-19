package ar.edu.utn.frba.dds.model.domain.user;

import ar.edu.utn.frba.dds.datasource.Fuente;
import ar.edu.utn.frba.dds.datasource.FuenteEstatica;
import ar.edu.utn.frba.dds.model.domain.coleccion.Coleccion;
import ar.edu.utn.frba.dds.model.domain.interfaces.Criterio;
import ar.edu.utn.frba.dds.model.domain.request.SolicitudDeEliminacion;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Administrador {
  public void crearColeccion(String titulo, String descripcion, FuenteEstatica fuente, Criterio criterioDePertenencia) {
    Coleccion nuevaColeccion = new Coleccion(titulo, descripcion, fuente, criterioDePertenencia);
  }

  public void aceptarSolicitud(SolicitudDeEliminacion solicitud) {
    solicitud.aceptar();
  }

  public void rechazarSolicitud(SolicitudDeEliminacion solicitud) {
    solicitud.rechazar();
  }
}
