package ar.utn.ba.ddsi.agregador.domain.coleccion.criterios;

import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CriterioLugar implements Criterio {
  private String tipo = "lugar";
  private Double lat;
  private Double lng;

  public CriterioLugar(Double lat, Double lng) {
    this.lat = lat;
    this.lng = lng;
  }

  public CriterioLugar(String pais) {
    this.lat = null;
    this.lng = null;
  }

  @Override
  public boolean cumple(Hecho hecho) {
    boolean cumpleLatitud = (lat == null || hecho.getLatitud().equals(lat));
    boolean cumpleLongitud = (lng == null || hecho.getLongitud().equals(lng));

    return cumpleLatitud && cumpleLongitud;
  }
}