package ar.edu.utn.frba.dds.model.domain.interfaces;

import ar.edu.utn.frba.dds.model.domain.hecho.Hecho;

public interface Criterio {
  boolean cumple(Hecho hecho);
}
