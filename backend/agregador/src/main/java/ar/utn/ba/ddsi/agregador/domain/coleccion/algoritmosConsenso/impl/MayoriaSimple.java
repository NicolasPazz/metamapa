package ar.utn.ba.ddsi.agregador.domain.coleccion.algoritmosConsenso.impl;

import ar.utn.ba.ddsi.agregador.domain.coleccion.algoritmosConsenso.AAlgoritmoConsenso;
import ar.utn.ba.ddsi.agregador.domain.coleccion.algoritmosConsenso.TipoAlgoritmoConsenso;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import java.util.List;

public class MayoriaSimple extends AAlgoritmoConsenso {
  private static final MayoriaSimple INSTANCE = new MayoriaSimple();
  private final TipoAlgoritmoConsenso nombre = TipoAlgoritmoConsenso.MAYORIA_SIMPLE;

  private MayoriaSimple() {}

  public static MayoriaSimple getInstance() {
    return INSTANCE;
  }

  @Override
  public TipoAlgoritmoConsenso getNombre() {
    return nombre;
  }

  @Override
  public Boolean esConsensuado(Hecho hecho, int fuentesTotales, List<Hecho> hechos) {
    return this.aparicionesMayorA(hecho, fuentesTotales / 2, hechos);
  }
}
