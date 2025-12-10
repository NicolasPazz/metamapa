package ar.utn.ba.ddsi.agregador.domain.coleccion.criterios;

import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CriterioLugar implements Criterio {
  private final TipoCriterio tipo = TipoCriterio.LUGAR;
  private Double lat;
  private Double lon;

  public CriterioLugar(Double lat, Double lon) {
    this.lat = lat;
    this.lon = lon;
  }

  /*
  public CriterioLugar(String pais) {
    this.lat = null;
    this.lon = null;
  }
  */

  @Override
  public boolean cumple(Hecho hecho) {
    final double EPSILON = 1e-6;

    boolean cumpleLatitud =
        (lat == null) ||
            (hecho.getLatitud() != null &&
                Math.abs(hecho.getLatitud() - lat) < EPSILON);

    boolean cumpleLongitud =
        (lon == null) ||
            (hecho.getLongitud() != null &&
                Math.abs(hecho.getLongitud() - lon) < EPSILON);

    return cumpleLatitud && cumpleLongitud;
  }

}