package ar.edu.utn.frba.dds.model.domain.criterioDePertenencia;

import ar.edu.utn.frba.dds.model.domain.hecho.Hecho;
import ar.edu.utn.frba.dds.model.domain.interfaces.Criterio;

import java.time.LocalDate;

public class CriterioHastaFecha implements Criterio {
    private LocalDate fecha;

    public CriterioHastaFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    @Override
    public boolean cumple(Hecho hecho){
        LocalDate fechaHecho = hecho.getFecha();
        return fechaHecho.isBefore(fecha) || fechaHecho.isEqual(fecha) ;
    }

    public LocalDate getFecha() {
        return fecha;
    }
}
