package ar.utn.ba.ddsi.servicio_fuente_estatica.domain.coleccion.criterioDePertenencia;

import ar.utn.ba.ddsi.servicio_fuente_estatica.domain.hecho.Hecho;

public interface Criterio {
  boolean cumple(Hecho hecho);
}
