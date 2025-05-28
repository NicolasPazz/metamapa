package ar.utn.ba.ddsi.monomodule.domain.coleccion.criterioDePertenencia;

import ar.utn.ba.ddsi.monomodule.domain.hecho.Hecho;

public interface Criterio {
  boolean cumple(Hecho hecho);
}
