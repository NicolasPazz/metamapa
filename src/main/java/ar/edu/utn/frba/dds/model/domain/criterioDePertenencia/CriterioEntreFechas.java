package ar.edu.utn.frba.dds.model.domain.criterioDePertenencia;
import ar.edu.utn.frba.dds.model.domain.hecho.Hecho;
import ar.edu.utn.frba.dds.model.domain.interfaces.Criterio;
import java.time.LocalDate;

public class CriterioEntreFechas implements Criterio {
    private LocalDate fechaInicial;
    private LocalDate fechaFinal;

    public CriterioEntreFechas(LocalDate fechaDeInicio, LocalDate fechaDeFin) {
        if(fechaDeInicio.isAfter(fechaDeFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }
        
        this.fechaInicial = fechaDeInicio;
        this.fechaFinal = fechaDeFin;
    }

    @Override
    public boolean cumple(Hecho hecho){
        LocalDate fecha = hecho.getFecha();
        return fecha.isEqual(fechaInicial) || fecha.isAfter(fechaInicial) &&
                fecha.isEqual(fechaFinal) || fecha.isBefore(fechaFinal);
    }

    public LocalDate getFechaDeInicio() {
        return fechaInicial;
    }

    public LocalDate getFechaDeFin() {
        return fechaFinal;
    }

}
