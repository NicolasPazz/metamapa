package ar.edu.utn.frba.dds.model.domain.criterioDePertenencia;

import ar.edu.utn.frba.dds.model.domain.hecho.Hecho;
import ar.edu.utn.frba.dds.model.domain.interfaces.Criterio;

public class CriterioTitulo implements Criterio {
    private String titulo;

    public CriterioTitulo(String titulo) {
        this.titulo = titulo;
    }

    @Override
    public boolean cumple(Hecho hecho) {
        return hecho.getTitulo().equals(titulo);
    }

    public String getTitulo() {
        return titulo;
    }
}
