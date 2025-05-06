package ar.edu.utn.frba.dds.domain.coleccion.criterioDePertenencia;

import ar.edu.utn.frba.dds.domain.hecho.Hecho;

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