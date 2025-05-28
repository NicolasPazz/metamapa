package ar.utn.ba.ddsi.fuentedinamica.domain.coleccion.criterioDePertenencia;

import ar.utn.ba.ddsi.fuentedinamica.domain.hecho.Hecho;

public interface Criterio {
  boolean cumple(Hecho hecho);
}
