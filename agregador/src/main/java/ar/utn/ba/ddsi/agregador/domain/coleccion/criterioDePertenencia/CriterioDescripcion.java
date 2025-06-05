package ar.utn.ba.ddsi.agregador.domain.coleccion.criterioDePertenencia;

import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho ;

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