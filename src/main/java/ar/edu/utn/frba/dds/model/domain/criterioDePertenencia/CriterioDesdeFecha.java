package ar.edu.utn.frba.dds.model.domain.criterioDePertenencia;

import ar.edu.utn.frba.dds.model.domain.hecho.Hecho;
import ar.edu.utn.frba.dds.model.domain.interfaces.Criterio;

import java.time.LocalDate;

public class CriterioDesdeFecha implements Criterio {

    private LocalDate fecha;

    public CriterioDesdeFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    @Override
    public boolean cumple(Hecho hecho){
        LocalDate fechaHecho = hecho.getFecha();
        return  fechaHecho.isAfter(fecha) || fechaHecho.equals(fecha) ;
    }

    public LocalDate getFecha() {
        return fecha;
    }
}
