package ar.utn.ba.ddsi.fuentedinamica.domain.coleccion.criterioDePertenencia;

import ar.utn.ba.ddsi.fuentedinamica.domain.hecho.Categoria;
import ar.utn.ba.ddsi.fuentedinamica.domain.hecho.Hecho;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CriterioCategoria implements Criterio {
    private Categoria categoria;

    @Override
    public boolean cumple(Hecho hecho){
        return hecho.esDeCategoria(categoria);
    }
}