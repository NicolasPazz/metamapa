package ar.edu.utn.frba.dds.domain.model.coleccion.criterioDePertenencia;

import ar.edu.utn.frba.dds.domain.model.hecho.Hecho;

public class CriterioLugarLatLng implements Criterio {
    private Double lat;
    private Double lng;


    public CriterioLugarLatLng(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public boolean cumple(Hecho hecho) {
        boolean cumpleLatitud = (lat == null || hecho.getLatitud().equals(lat));
        boolean cumpleLongitud = (lng == null || hecho.getLongitud().equals(lng));

        return cumpleLatitud && cumpleLongitud;
    }
}
