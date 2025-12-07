package ar.utn.ba.ddsi.agregador.domain.algoritmosConsenso.impl;

import ar.utn.ba.ddsi.agregador.domain.algoritmosConsenso.AAlgoritmoConsenso;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import java.util.List;

public class mayoriaSimple extends AAlgoritmoConsenso {

  @Override
  public Boolean esConsensuado(Hecho hecho, int fuentesTotales, List<Hecho> hechos) {
    return this.aparicionesMayorA(hecho, fuentesTotales / 2, hechos);
  }
}
