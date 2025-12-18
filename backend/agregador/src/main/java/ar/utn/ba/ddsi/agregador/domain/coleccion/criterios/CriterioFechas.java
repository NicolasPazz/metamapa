package ar.utn.ba.ddsi.agregador.domain.coleccion.criterios;

import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho ;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class CriterioFechas implements Criterio {
    @JsonIgnore
    private final TipoCriterio tipo = TipoCriterio.FECHAS;
    private LocalDateTime fechaInicial;
    private LocalDateTime fechaFinal;
    private TipoFecha tipoFecha;

    public CriterioFechas(LocalDate fechaDeInicio, LocalDate fechaDeFin, TipoFecha tipoFecha) {
        if (fechaDeInicio != null && fechaDeFin != null && fechaDeInicio.isAfter(fechaDeFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }

        this.fechaInicial = fechaDeInicio != null ? fechaDeInicio.atStartOfDay() : null;
        this.fechaFinal = fechaDeFin != null ? fechaDeFin.atTime(23, 59, 59) : null;
        this.tipoFecha = tipoFecha;
    }

    public CriterioFechas(LocalDateTime fechaDeInicio, LocalDateTime fechaDeFin, TipoFecha tipoFecha) {
        if (fechaDeInicio != null && fechaDeFin != null && fechaDeInicio.isAfter(fechaDeFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }

        this.fechaInicial = fechaDeInicio;
        this.fechaFinal = fechaDeFin;
        this.tipoFecha = tipoFecha;
    }

    @Override
    @JsonIgnore
    public boolean cumple(Hecho hecho) {
        LocalDateTime fecha = switch (tipoFecha) {
            case CARGA -> hecho.getFechaDeCarga();
            case ACONTECIMIENTO -> hecho.getFechaDeAcontecimiento();
        };

        boolean cumpleDesde = (fechaInicial == null || !fecha.isBefore(fechaInicial));
        boolean cumpleHasta = (fechaFinal == null || !fecha.isAfter(fechaFinal));

        return cumpleDesde && cumpleHasta;
    }
}