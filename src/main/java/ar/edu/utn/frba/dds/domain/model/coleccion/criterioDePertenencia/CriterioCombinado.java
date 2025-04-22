package ar.edu.utn.frba.dds.domain.model.coleccion.criterioDePertenencia;

import ar.edu.utn.frba.dds.domain.model.hecho.Hecho;

import java.util.List;

public class CriterioCombinado implements Criterio{
    private List<Criterio> criterios;

    public CriterioCombinado(List<Criterio> criterios) {
        this.criterios = criterios;
    }

    @Override
    public boolean cumple(Hecho hecho){
        return criterios.stream().allMatch(criterio -> criterio.cumple(hecho));
    }

}
