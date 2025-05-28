package ar.utn.ba.ddsi.monomodule.domain.coleccion.criterioDePertenencia;

import ar.utn.ba.ddsi.monomodule.domain.hecho.Hecho;

public class CriterioLugar implements Criterio {
    private Double lat;
    private Double lng;
    private String pais;

    public CriterioLugar(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
        this.pais = null;
    }

    public CriterioLugar(String pais) {
        this.lat = null;
        this.lng = null;
        this.pais = pais;
    }

    @Override
    public boolean cumple(Hecho hecho) {
        boolean cumpleLatitud = (lat == null || hecho.getLatitud().equals(lat));
        boolean cumpleLongitud = (lng == null || hecho.getLongitud().equals(lng));

        // TODO: implementar GeoTools para filtrar por pais

        return cumpleLatitud && cumpleLongitud;
    }
}