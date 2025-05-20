package ar.utn.frba.dds.metamapa.domain.coleccion.criterioDePertenencia;

import ar.utn.frba.dds.metamapa.domain.hecho.Hecho;

public class CriterioTitulo implements Criterio {
    private String titulo;

    public CriterioTitulo(String titulo) {
        this.titulo = titulo.toLowerCase();
    }

    @Override
    public boolean cumple(Hecho hecho) {
        return hecho.getTitulo().toLowerCase().contains(titulo);
    }
}