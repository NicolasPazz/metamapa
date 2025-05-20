package ar.utn.frba.dds.metamapa.domain.coleccion.criterioDePertenencia;

import ar.utn.frba.dds.metamapa.domain.hecho.Hecho;

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