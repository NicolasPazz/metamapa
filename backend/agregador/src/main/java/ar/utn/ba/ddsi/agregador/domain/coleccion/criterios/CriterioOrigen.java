package ar.utn.ba.ddsi.agregador.domain.coleccion.criterios;

import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.domain.hecho.OrigenHecho;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CriterioOrigen implements Criterio{
  private final TipoCriterio tipo = TipoCriterio.ORIGEN;
  private OrigenHecho origen;

  public CriterioOrigen(OrigenHecho origen) {
    this.origen = origen;
  }

  public boolean cumple(Hecho hecho) {
    return this.origen == hecho.getOrigen();
  }
}