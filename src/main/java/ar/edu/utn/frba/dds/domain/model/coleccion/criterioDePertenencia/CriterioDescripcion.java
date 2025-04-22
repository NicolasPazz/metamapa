package ar.edu.utn.frba.dds.domain.model.coleccion.criterioDePertenencia;

import ar.edu.utn.frba.dds.domain.model.hecho.Hecho;

public class CriterioDescripcion implements Criterio {
    private String descripcion;


    public CriterioDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public boolean cumple(Hecho hecho){
        return hecho.getDescripcion().equalsIgnoreCase(descripcion);
    }
}
