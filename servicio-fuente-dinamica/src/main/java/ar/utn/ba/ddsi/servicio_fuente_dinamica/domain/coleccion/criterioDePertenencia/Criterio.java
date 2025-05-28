package ar.utn.ba.ddsi.servicio_fuente_dinamica.domain.coleccion.criterioDePertenencia;

import ar.utn.ba.ddsi.servicio_fuente_dinamica.domain.hecho.Hecho;

public interface Criterio {
  boolean cumple(Hecho hecho);
}
