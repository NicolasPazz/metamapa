package ar.utn.ba.ddsi.agregador.domain.coleccion.criterios;

import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho ;

public interface Criterio {
  boolean cumple(Hecho hecho);
}
