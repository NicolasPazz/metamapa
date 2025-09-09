package ar.utn.ba.ddsi.agregador.domain.coleccion.criterios;

import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho ;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CriterioCombinado implements Criterio {
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