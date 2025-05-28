package ar.utn.ba.ddsi.servicio_agregador.domain.user;

import ar.utn.ba.ddsi.servicio_agregador.service.IFuenteService;
import ar.utn.ba.ddsi.servicio_agregador.domain.coleccion.Coleccion;
import ar.utn.ba.ddsi.servicio_agregador.domain.coleccion.criterioDePertenencia.Criterio;
import ar.utn.ba.ddsi.servicio_agregador.domain.request.SolicitudDeEliminacion;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Administrador {
  public Coleccion crearColeccion(String titulo, String descripcion, IFuenteService fuente, Criterio criterioDePertenencia) {
    return new Coleccion(titulo, descripcion, fuente, criterioDePertenencia);
  }

  public void aceptarSolicitud(SolicitudDeEliminacion solicitud) {
    solicitud.aceptar();
  }

  public void rechazarSolicitud(SolicitudDeEliminacion solicitud) {
    solicitud.rechazar();
  }
}
