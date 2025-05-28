package ar.utn.ba.ddsi.estadisticas.domain.coleccion.criterioDePertenencia;

import ar.utn.ba.ddsi.estadisticas.domain.hecho.Hecho;

public interface Criterio {
  boolean cumple(Hecho hecho);
}
