package ar.utn.frba.dds.metamapa.domain.coleccion.criterioDePertenencia;

import ar.utn.frba.dds.metamapa.domain.hecho.Hecho;

public interface Criterio {
  boolean cumple(Hecho hecho);
}
