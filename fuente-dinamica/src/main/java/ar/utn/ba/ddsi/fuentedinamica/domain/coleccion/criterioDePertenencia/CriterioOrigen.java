package ar.utn.ba.ddsi.fuentedinamica.domain.coleccion.criterioDePertenencia;

import ar.utn.ba.ddsi.fuentedinamica.domain.hecho.Hecho;
import ar.utn.ba.ddsi.fuentedinamica.domain.hecho.OrigenHecho;

public class CriterioOrigen {
  private OrigenHecho origen;

  public CriterioOrigen(OrigenHecho origen) {
    this.origen = origen;
  }

  public boolean cumple(Hecho hecho) {
    return this.origen == hecho.getOrigen();
  }
}