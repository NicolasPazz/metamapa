package ar.edu.utn.frba.dds.domain.model.coleccion.criterioDePertenencia;

import ar.edu.utn.frba.dds.domain.model.hecho.Hecho;

public class CriterioCategoria implements Criterio {
    private String categoria;

    public CriterioCategoria(String categoria) {
        this.categoria = categoria;
    }

    @Override
    public boolean cumple(Hecho hecho){
        return hecho.getCategoria().equalsIgnoreCase(categoria);
    }
}
