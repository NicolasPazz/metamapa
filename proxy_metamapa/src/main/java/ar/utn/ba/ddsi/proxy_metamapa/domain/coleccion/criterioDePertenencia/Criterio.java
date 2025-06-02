package ar.utn.ba.ddsi.proxy_metamapa.domain.coleccion.criterioDePertenencia;

import ar.utn.ba.ddsi.proxy_metamapa.domain.hecho.Hecho;

public interface Criterio {
  boolean cumple(Hecho hecho);
}
