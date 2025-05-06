package ar.edu.utn.frba.dds.domain.coleccion.criterioDePertenencia;

import ar.edu.utn.frba.dds.domain.hecho.Hecho;
import ar.edu.utn.frba.dds.domain.hecho.OrigenHecho;

public class CriterioOrigen {
  private OrigenHecho origen;

  public CriterioOrigen(OrigenHecho origen) {
    this.origen = origen;
  }

  public boolean cumple(Hecho hecho) {
    return this.origen == hecho.getOrigen();
  }
}