package ar.utn.ba.ddsi.servicio_estadisticas.domain.coleccion.criterioDePertenencia;

import ar.utn.ba.ddsi.servicio_estadisticas.domain.hecho.Hecho;

public interface Criterio {
  boolean cumple(Hecho hecho);
}
