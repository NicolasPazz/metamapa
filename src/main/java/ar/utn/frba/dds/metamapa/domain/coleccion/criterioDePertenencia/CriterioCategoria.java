package ar.utn.frba.dds.metamapa.domain.coleccion.criterioDePertenencia;

import ar.utn.frba.dds.metamapa.domain.hecho.Categoria;
import ar.utn.frba.dds.metamapa.domain.hecho.Hecho;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CriterioCategoria implements Criterio {
    private Categoria categoria;

    @Override
    public boolean cumple(Hecho hecho){
        return hecho.esDeCategoria(categoria);
    }
}