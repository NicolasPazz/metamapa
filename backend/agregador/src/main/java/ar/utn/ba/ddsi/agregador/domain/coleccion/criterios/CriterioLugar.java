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
  private String provincia;

  public CriterioLugar(Double lat, Double lon) {
    this.lat = lat;
    this.lon = lon;
    this.provincia = null;
  }

  public CriterioLugar(String provincia) {
    this.lat = null;
    this.lon = null;
    this.provincia = provincia;
  }

  @Override
  public boolean cumple(Hecho hecho) {
    // Si se especificó una provincia, verificar que coincida
    if (provincia != null && !provincia.isEmpty()) {
      return hecho.getProvincia() != null &&
             hecho.getProvincia().getNombre().equalsIgnoreCase(provincia);
    }

    // Si se especificaron coordenadas, verificar que coincidan
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