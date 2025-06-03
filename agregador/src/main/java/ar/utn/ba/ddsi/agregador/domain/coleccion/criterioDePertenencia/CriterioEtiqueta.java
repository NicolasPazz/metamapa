package ar.utn.ba.ddsi.agregador.domain.coleccion.criterioDePertenencia;

import ar.utn.ba.ddsi.agregador.domain.coleccion.criterioDePertenencia.Criterio ;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho ;

public class CriterioEtiqueta implements Criterio {
  private final String etiqueta;

  public CriterioEtiqueta(String etiqueta) {
    this.etiqueta = etiqueta.toLowerCase();
  }

  @Override
  public boolean cumple(Hecho hecho) {
    return hecho.contieneEtiqueta(etiqueta);
  }
}