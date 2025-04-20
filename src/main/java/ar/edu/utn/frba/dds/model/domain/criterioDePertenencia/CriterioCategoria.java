package ar.edu.utn.frba.dds.model.domain.criterioDePertenencia;

import ar.edu.utn.frba.dds.model.domain.hecho.Hecho;
import ar.edu.utn.frba.dds.model.domain.interfaces.Criterio;

public class CriterioCategoria implements Criterio {
    private String categoria;

    public CriterioCategoria(String categoria) {
        this.categoria = categoria;
    }

    @Override
    public boolean cumple(Hecho hecho){
        return categoria.equals(hecho.getCategoria());
    }

    public String getCategoria() {
        return categoria;
    }
}
