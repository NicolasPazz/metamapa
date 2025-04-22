package ar.edu.utn.frba.dds.domain.model.coleccion.criterioDePertenencia;

import ar.edu.utn.frba.dds.domain.model.hecho.Hecho;

public class CriterioTitulo implements Criterio {
    private String titulo;

    public CriterioTitulo(String titulo) {
        this.titulo = titulo;
    }

    @Override
    public boolean cumple(Hecho hecho) {
        return hecho.getTitulo().equalsIgnoreCase(titulo);
    }
}
