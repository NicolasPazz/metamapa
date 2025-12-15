package ar.utn.ba.ddsi.agregador.domain.coleccion.algoritmosConsenso;

import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import java.util.List;
import java.util.Objects;
import lombok.Getter;

@Getter
public abstract class AAlgoritmoConsenso {
  private static final double EPSILON_COORD = 1e-6;

  public abstract TipoAlgoritmoConsenso getNombre();

  public abstract Boolean esConsensuado(Hecho hecho, int fuentesTotales, List<Hecho> hechos);

  public int contarApariciones(Hecho hecho, List<Hecho> hechos) {
    return (int) hechos.stream()
        .filter(h -> sonSimilares(hecho, h))
        .count();
  }

  private boolean sonSimilares(Hecho h1, Hecho h2) {
    return igualesIgnoreCaseTrim(h1.getTitulo(), h2.getTitulo())
        && igualesIgnoreCaseTrim(h1.getDescripcion(), h2.getDescripcion())
        && igualesCoord(h1.getLatitud(), h2.getLatitud())
        && igualesCoord(h1.getLongitud(), h2.getLongitud())
        && Objects.equals(h1.getFechaDeAcontecimiento(), h2.getFechaDeAcontecimiento());
  }

  private boolean igualesIgnoreCaseTrim(String a, String b) {
    if (a == null || b == null) return Objects.equals(a, b);
    return a.trim().equalsIgnoreCase(b.trim());
  }

  private boolean igualesCoord(Double a, Double b) {
    if (a == null || b == null) return Objects.equals(a, b);
    return Math.abs(a - b) < EPSILON_COORD;
  }

  public Boolean aparicionesMayorA(Hecho hecho, int comparador, List<Hecho> hechos) {
    return contarApariciones(hecho, hechos) >= comparador;
  }
}
