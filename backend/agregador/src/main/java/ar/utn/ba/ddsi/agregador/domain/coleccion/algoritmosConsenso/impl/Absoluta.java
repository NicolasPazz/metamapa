package ar.utn.ba.ddsi.agregador.domain.coleccion.algoritmosConsenso.impl;

import ar.utn.ba.ddsi.agregador.domain.coleccion.algoritmosConsenso.AAlgoritmoConsenso;
import ar.utn.ba.ddsi.agregador.domain.coleccion.algoritmosConsenso.TipoAlgoritmoConsenso;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import java.util.List;

public class Absoluta extends AAlgoritmoConsenso {
  private static final Absoluta INSTANCE = new Absoluta();
  private final TipoAlgoritmoConsenso nombre = TipoAlgoritmoConsenso.ABSOLUTA;

  private Absoluta() {}

  public static Absoluta getInstance() {
    return INSTANCE;
  }

  @Override
  public TipoAlgoritmoConsenso getNombre() {
    return nombre;
  }

  @Override
  public Boolean esConsensuado(Hecho hecho, int fuentesTotales, List<Hecho> hechos) {
    return this.aparicionesMayorA(hecho, fuentesTotales, hechos);
  }
}
