package ar.utn.ba.ddsi.monomodule.domain.coleccion.criterioDePertenencia;

import ar.utn.ba.ddsi.monomodule.domain.hecho.Hecho;
import ar.utn.ba.ddsi.monomodule.domain.hecho.OrigenHecho;

public class CriterioOrigen {
  private OrigenHecho origen;

  public CriterioOrigen(OrigenHecho origen) {
    this.origen = origen;
  }

  public boolean cumple(Hecho hecho) {
    return this.origen == hecho.getOrigen();
  }
}