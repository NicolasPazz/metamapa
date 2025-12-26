package ar.utn.ba.ddsi.agregador.domain.coleccion.criterios;

import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CriterioLugar implements Criterio {
  @JsonIgnore
  private final TipoCriterio tipo = TipoCriterio.LUGAR;
  private Double lat;
  private Double lon;
  private Double radio;
  private String provincia;

  public CriterioLugar(Double lat, Double lon) {
    this.lat = lat;
    this.lon = lon;
    this.radio = null;
    this.provincia = null;
  }

  public CriterioLugar(Double lat, Double lon, Double radio) {
    this.lat = lat;
    this.lon = lon;
    this.radio = radio;
    this.provincia = null;
  }

  public CriterioLugar(String provincia) {
    this.lat = null;
    this.lon = null;
    this.radio = null;
    this.provincia = provincia;
  }

  @Override
  public boolean cumple(Hecho hecho) {
    if (provincia != null && !provincia.isEmpty()) {
      return hecho.getProvincia() != null &&
             hecho.getProvincia().getNombre().equalsIgnoreCase(provincia);
    }

    if (lat != null && lon != null && hecho.getLatitud() != null && hecho.getLongitud() != null) {
      if (radio != null && radio > 0) {
        double distancia = calcularDistancia(lat, lon, hecho.getLatitud(), hecho.getLongitud());
        return distancia <= radio;
      } else {
        final double EPSILON = 1e-6;
        return Math.abs(hecho.getLatitud() - lat) < EPSILON &&
               Math.abs(hecho.getLongitud() - lon) < EPSILON;
      }
    }

    return false;
  }

  private double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
    final int RADIO_TIERRA_KM = 6371;
    
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);
    
    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
               Math.sin(dLon / 2) * Math.sin(dLon / 2);
    
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    
    return RADIO_TIERRA_KM * c;
  }

}