package ar.utn.ba.ddsi.agregador.factory;

import ar.utn.ba.ddsi.agregador.domain.coleccion.criterios.*;
import ar.utn.ba.ddsi.agregador.domain.dto.input.CriterioInputDTO;
import ar.utn.ba.ddsi.agregador.domain.hecho.OrigenHecho;
import org.springframework.stereotype.Component;

import java.util.Arrays;


@Component
public class CriterioDePertenenciaFactory {

    public Criterio crearCriterioDesdeDTO(CriterioInputDTO dto) {
        if (dto.getTipo() != null && dto.getTipo().trim().isEmpty()){
            throw new IllegalArgumentException("Debe especificar el tipo de Criterio");
        }

        if (dto.getTipo() == null){
            return null;
        }else{
            switch (dto.getTipo().toLowerCase()) {
                case "fecha":
                    return crearCriterioFecha(dto);
                case "igualdad":
                    return crearCriterioIgualdad(dto);
                case "lugar":
                    return crearCriterioLugar(dto);
                case "origen":
                    return crearCriterioOrigen(dto);
                default:
                    throw new IllegalArgumentException("Tipo de criterio desconocido: " + dto.getTipo());
            }
        }
    }

    private static Criterio crearCriterioFecha(CriterioInputDTO dto) {
        if (dto.getTipoFecha() == null || dto.getTipoFecha().isBlank()) {
            throw new IllegalArgumentException("El campo 'tipo_fecha' es obligatorio para el criterio 'fecha'");
        }

        TipoFecha tipoFecha;
        try {
            tipoFecha = TipoFecha.valueOf(dto.getTipoFecha().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Valor de 'tipo_fecha' inválido. Debe ser 'CARGA' o 'ACONTECIMIENTO'");
        }

        return new CriterioFechas(
                dto.getFechaInicial(),
                dto.getFechaFinal(),
                tipoFecha
        );
    }

    private static Criterio crearCriterioIgualdad(CriterioInputDTO dto) {
        if (dto.getTipoDeDato() == null || dto.getTipoDeDato().isBlank()) {
            throw new IllegalArgumentException("El campo 'tipo_dato' es obligatorio para el criterio 'igualdad'");
        }
        if (dto.getCadenaAComparar() == null || dto.getCadenaAComparar().isBlank()) {
            throw new IllegalArgumentException("El campo 'cadena_a_comparar' es obligatorio para el criterio 'igualdad'");
        }

        TipoDato tipoDato;
        try {
            tipoDato = TipoDato.valueOf(dto.getTipoDeDato().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Valor inválido para 'tipo_dato'. Debe ser uno de: " +
                    Arrays.toString(TipoDato.values()));
        }
        return new CriterioIgualdad(dto.getCadenaAComparar().trim(), tipoDato);
    }

    private static Criterio crearCriterioLugar(CriterioInputDTO dto) {
        return new CriterioLugar(dto.getLat(), dto.getLng());
    }

    private static Criterio crearCriterioOrigen(CriterioInputDTO dto) {
        if (dto.getOrigen() == null || dto.getOrigen().isBlank()) {
            throw new IllegalArgumentException("El campo 'origen' es obligatorio para el criterio 'origen'");
        }

        OrigenHecho origenHecho;
        try{
            origenHecho = OrigenHecho.valueOf(dto.getOrigen().toUpperCase());
        }catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Valor invalido para 'origen'. Debe ser uno de: "+
                    Arrays.toString(OrigenHecho.values()));
        }

        return new CriterioOrigen(origenHecho);
    }
}
