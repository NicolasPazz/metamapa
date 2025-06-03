package ar.utn.ba.ddsi.agregador.domain.coleccion.criterioDePertenencia;

import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho ;

public interface Criterio {
  boolean cumple(Hecho hecho);
}
