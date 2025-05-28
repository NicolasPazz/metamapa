package ar.utn.ba.ddsi.servicio_fuente_proxy.domain.coleccion.criterioDePertenencia;

import ar.utn.ba.ddsi.servicio_fuente_proxy.domain.coleccion.criterioDePertenencia.Criterio;
import ar.utn.ba.ddsi.servicio_fuente_proxy.domain.hecho.Hecho;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CriterioCombinado implements ar.utn.ba.ddsi.servicio_fuente_proxy.domain.coleccion.criterioDePertenencia.Criterio {
    private List<ar.utn.ba.ddsi.servicio_fuente_proxy.domain.coleccion.criterioDePertenencia.Criterio> criterios;

    public CriterioCombinado(List<ar.utn.ba.ddsi.servicio_fuente_proxy.domain.coleccion.criterioDePertenencia.Criterio> criterios) {
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