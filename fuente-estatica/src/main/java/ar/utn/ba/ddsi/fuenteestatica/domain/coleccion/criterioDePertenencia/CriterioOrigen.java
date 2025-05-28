package ar.utn.ba.ddsi.fuenteestatica.domain.coleccion.criterioDePertenencia;

import ar.utn.ba.ddsi.fuenteestatica.domain.hecho.Hecho;
import ar.utn.ba.ddsi.fuenteestatica.domain.hecho.OrigenHecho;

public class CriterioOrigen {
  private OrigenHecho origen;

  public CriterioOrigen(OrigenHecho origen) {
    this.origen = origen;
  }

  public boolean cumple(Hecho hecho) {
    return this.origen == hecho.getOrigen();
  }
}