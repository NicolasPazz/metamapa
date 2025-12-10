package ar.utn.ba.ddsi.agregador.domain.coleccion.algoritmosConsenso.impl;

import ar.utn.ba.ddsi.agregador.domain.coleccion.algoritmosConsenso.AAlgoritmoConsenso;
import ar.utn.ba.ddsi.agregador.domain.coleccion.algoritmosConsenso.TipoAlgoritmoConsenso;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import java.util.List;

public class MultiplesMenciones extends AAlgoritmoConsenso {
  private static final MultiplesMenciones INSTANCE = new MultiplesMenciones();
  private final TipoAlgoritmoConsenso nombre = TipoAlgoritmoConsenso.MULTIPLES_MENCIONES;

  private MultiplesMenciones() {}

  public static MultiplesMenciones getInstance() {
    return INSTANCE;
  }

  @Override
  public TipoAlgoritmoConsenso getNombre() {
    return nombre;
  }

  @Override
  public Boolean esConsensuado(Hecho hecho, int fuentesTotales, List<Hecho> hechos) {
    return this.aparicionesMayorA(hecho, 2, hechos) && !tieneConflictosDeTitulo(hecho, hechos);
  }

  private boolean tieneConflictosDeTitulo(Hecho hecho, List<Hecho> hechos) {
    return contarApariciones(hecho, hechos) != contarConTituloIgual(hecho, hechos);
  }

  private long contarConTituloIgual(Hecho hecho, List<Hecho> hechos) {
    return (int)  hechos.parallelStream()
        .filter(h -> normalizar(h.getTitulo()).equals(normalizar(hecho.getTitulo())))
        .count();
  }

  private String normalizar(String s) {
    return s == null ? "" : s.toLowerCase().trim();
  }
}
