package ar.utn.ba.ddsi.agregador.domain.coleccion.criterioDePertenencia;

import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho ;

import java.time.LocalDate;

public class CriterioFechas implements Criterio {
    private LocalDate fechaInicial;
    private LocalDate fechaFinal;
    private TipoFecha tipoFecha;

    public CriterioFechas(LocalDate fechaDeInicio, LocalDate fechaDeFin, TipoFecha tipoFecha) {
        if (fechaDeInicio != null && fechaDeFin != null && fechaDeInicio.isAfter(fechaDeFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }

        this.fechaInicial = fechaDeInicio;
        this.fechaFinal = fechaDeFin;
        this.tipoFecha = tipoFecha;
    }

    @Override
    public boolean cumple(Hecho hecho) {
        LocalDate fecha = switch (tipoFecha) {
            case CARGA -> hecho.getFechaDeCarga().toLocalDate();
            case ACONTECIMIENTO -> hecho.getFechaDeAcontecimiento();
        };

        boolean cumpleDesde = (fechaInicial == null || !fecha.isBefore(fechaInicial));
        boolean cumpleHasta = (fechaFinal == null || !fecha.isAfter(fechaFinal));

        return cumpleDesde && cumpleHasta;
    }
}