package ar.utn.ba.ddsi.servicio_estadisticas.domain.user;

import ar.utn.ba.ddsi.servicio_estadisticas.service.IFuenteService;
import ar.utn.ba.ddsi.servicio_estadisticas.domain.coleccion.Coleccion;
import ar.utn.ba.ddsi.servicio_estadisticas.domain.coleccion.criterioDePertenencia.Criterio;
import ar.utn.ba.ddsi.servicio_estadisticas.domain.request.SolicitudDeEliminacion;
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
