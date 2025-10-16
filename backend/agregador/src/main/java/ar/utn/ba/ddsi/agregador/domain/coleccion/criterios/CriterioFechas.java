package ar.utn.ba.ddsi.agregador.domain.coleccion.criterios;

import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho ;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class CriterioFechas implements Criterio {
    private String tipo = "fechas";
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