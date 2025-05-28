package ar.utn.ba.ddsi.servicio_fuente_estatica.domain.coleccion.criterioDePertenencia;

import ar.utn.ba.ddsi.servicio_fuente_estatica.domain.coleccion.criterioDePertenencia.Criterio;
import ar.utn.ba.ddsi.servicio_fuente_estatica.domain.hecho.Hecho;

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