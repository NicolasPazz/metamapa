package ar.utn.ba.ddsi.dinamica.domain.coleccion.criterioDePertenencia;

import ar.utn.ba.ddsi.dinamica.domain.hecho.Hecho;

public interface Criterio {
  boolean cumple(Hecho hecho);
}
