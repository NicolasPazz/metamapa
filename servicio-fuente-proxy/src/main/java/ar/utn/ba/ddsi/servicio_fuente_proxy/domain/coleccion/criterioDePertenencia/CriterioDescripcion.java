package ar.utn.ba.ddsi.servicio_fuente_proxy.domain.coleccion.criterioDePertenencia;

import ar.utn.ba.ddsi.servicio_fuente_proxy.domain.coleccion.criterioDePertenencia.Criterio;
import ar.utn.ba.ddsi.servicio_fuente_proxy.domain.hecho.Hecho;

public class CriterioDescripcion implements Criterio {
    private String descripcion;


    public CriterioDescripcion(String descripcion) {
        this.descripcion = descripcion.toLowerCase();
    }

    @Override
    public boolean cumple(Hecho hecho){
        return hecho.getDescripcion().toLowerCase().contains(descripcion);
    }
}