package ar.edu.utn.frba.dds.domain.coleccion.criterioDePertenencia;

import ar.edu.utn.frba.dds.domain.hecho.Hecho;

public interface Criterio {
  boolean cumple(Hecho hecho);
}
