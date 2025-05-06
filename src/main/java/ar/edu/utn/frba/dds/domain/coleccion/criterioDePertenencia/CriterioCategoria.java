package ar.edu.utn.frba.dds.domain.coleccion.criterioDePertenencia;

import ar.edu.utn.frba.dds.domain.hecho.Hecho;

public class CriterioCategoria implements Criterio {
    private String categoria;

    public CriterioCategoria(String categoria) {
        this.categoria = categoria.toLowerCase();
    }

    @Override
    public boolean cumple(Hecho hecho){
        return hecho.getCategoria().toLowerCase().contains(categoria);
    }
}