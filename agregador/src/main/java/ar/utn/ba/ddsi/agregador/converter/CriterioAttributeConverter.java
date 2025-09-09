package ar.utn.ba.ddsi.agregador.converter;

import ar.utn.ba.ddsi.agregador.domain.coleccion.criterios.*;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class CriterioAttributeConverter implements AttributeConverter<Criterio, String> {
    String criterioCol = "";
    @Override
    public String convertToDatabaseColumn(Criterio criterio) {
        if(criterio == null) return null;
        else if(criterio instanceof CriterioFechas) criterioCol = "fechas";
        else if(criterio instanceof CriterioFuente) criterioCol = "fuente";
        else if(criterio instanceof CriterioIgualdad) criterioCol = "igualdad";
        else if(criterio instanceof CriterioLugar) criterioCol = "lugar";
        else if(criterio instanceof CriterioOrigen) criterioCol = "origen";
        else if(criterio instanceof CriterioCombinado) criterioCol = "combinado";

        return criterioCol;
    }

    @Override
    public Criterio convertToEntityAttribute(String s) {
        if(s == null) return null;

        Criterio criterio = null;

        switch(s){
            case "fechas": criterio = new CriterioFechas(); break;
            case "fuente": criterio = new CriterioFuente(); break;
            case "igualdad": criterio = new CriterioIgualdad(); break;
            case "lugar": criterio = new CriterioLugar(); break;
            case "origen": criterio = new CriterioOrigen(); break;
            case "combinado": criterio = new CriterioCombinado(); break;
        }

        return criterio;
    }
}
