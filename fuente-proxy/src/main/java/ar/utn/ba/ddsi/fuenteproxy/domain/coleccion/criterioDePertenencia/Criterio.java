package ar.utn.ba.ddsi.fuenteproxy.domain.coleccion.criterioDePertenencia;

import ar.utn.ba.ddsi.fuenteproxy.domain.hecho.Hecho;

public interface Criterio {
  boolean cumple(Hecho hecho);
}
