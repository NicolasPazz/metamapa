package ar.utn.frba.dds.metamapa.domain.coleccion.criterioDePertenencia;

import ar.utn.frba.dds.metamapa.domain.hecho.Hecho;
import ar.utn.frba.dds.metamapa.domain.hecho.OrigenHecho;

public class CriterioOrigen {
  private OrigenHecho origen;

  public CriterioOrigen(OrigenHecho origen) {
    this.origen = origen;
  }

  public boolean cumple(Hecho hecho) {
    return this.origen == hecho.getOrigen();
  }
}