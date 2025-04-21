package ar.edu.utn.frba.dds.domain.model.coleccion.criterioDePertenencia;
import ar.edu.utn.frba.dds.domain.model.hecho.Hecho;
import java.time.LocalDate;

public class CriterioEntreFechas implements Criterio {
    private LocalDate fechaInicial;
    private LocalDate fechaFinal;

    public CriterioEntreFechas(LocalDate fechaDeInicio, LocalDate fechaDeFin) {
        if (fechaDeInicio != null && fechaDeFin != null && fechaDeInicio.isAfter(fechaDeFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }

        this.fechaInicial = fechaDeInicio;
        this.fechaFinal = fechaDeFin;
    }

    @Override
    public boolean cumple(Hecho hecho) {
        LocalDate fecha = hecho.getFecha();

        boolean cumpleDesde = (fechaInicial == null || !fecha.isBefore(fechaInicial));
        boolean cumpleHasta = (fechaFinal == null || !fecha.isAfter(fechaFinal));

        return cumpleDesde && cumpleHasta;
    }
}
