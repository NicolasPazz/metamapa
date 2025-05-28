package ar.utn.ba.ddsi.estadisticas.domain.coleccion.criterioDePertenencia;

import ar.utn.ba.ddsi.estadisticas.domain.hecho.Hecho;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CriterioCombinado implements Criterio{
    private List<Criterio> criterios;

    public CriterioCombinado(List<Criterio> criterios) {
        this.criterios = criterios;
    }

    public void agregarCriterio(Criterio criterio){
        this.criterios.add(criterio);
    }

    @Override
    public boolean cumple(Hecho hecho){
        return criterios.stream().allMatch(criterio -> criterio.cumple(hecho));
    }

}