package ar.utn.ba.ddsi.servicio_agregador.domain.coleccion.criterioDePertenencia;

import ar.utn.ba.ddsi.servicio_agregador.domain.hecho.Hecho;

public interface Criterio {
  boolean cumple(Hecho hecho);
}
