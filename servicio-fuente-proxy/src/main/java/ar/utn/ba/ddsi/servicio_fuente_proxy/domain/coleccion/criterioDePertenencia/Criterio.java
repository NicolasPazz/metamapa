package ar.utn.ba.ddsi.servicio_fuente_proxy.domain.coleccion.criterioDePertenencia;

import ar.utn.ba.ddsi.servicio_fuente_proxy.domain.hecho.Hecho;

public interface Criterio {
  boolean cumple(Hecho hecho);
}
