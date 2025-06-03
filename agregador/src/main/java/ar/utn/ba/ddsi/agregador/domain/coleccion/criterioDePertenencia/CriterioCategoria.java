package ar.utn.ba.ddsi.agregador.domain.coleccion.criterioDePertenencia;

import ar.utn.ba.ddsi.agregador.domain.coleccion.criterioDePertenencia.Criterio ;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho ;

public class CriterioCategoria implements Criterio {
    private String categoria;

    public CriterioCategoria(String categoria) {
        this.categoria = categoria.toLowerCase();
    }

    @Override
    public boolean cumple(Hecho hecho){
        return hecho.getCategoria().getNombre().toLowerCase().contains(categoria);
    }
}