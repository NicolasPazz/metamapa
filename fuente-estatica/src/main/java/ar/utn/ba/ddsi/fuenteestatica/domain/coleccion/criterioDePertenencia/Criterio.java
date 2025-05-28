package ar.utn.ba.ddsi.fuenteestatica.domain.coleccion.criterioDePertenencia;

import ar.utn.ba.ddsi.fuenteestatica.domain.hecho.Hecho;

public interface Criterio {
  boolean cumple(Hecho hecho);
}
