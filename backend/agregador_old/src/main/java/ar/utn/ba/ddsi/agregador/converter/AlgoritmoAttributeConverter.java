package ar.utn.ba.ddsi.agregador.converter;

import ar.utn.ba.ddsi.agregador.domain.algoritmosConsenso.AAlgoritmoConsenso;
import ar.utn.ba.ddsi.agregador.domain.algoritmosConsenso.impl.absoluta;
import ar.utn.ba.ddsi.agregador.domain.algoritmosConsenso.impl.mayoriaSimple;
import ar.utn.ba.ddsi.agregador.domain.algoritmosConsenso.impl.multiplesMenciones;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class AlgoritmoAttributeConverter implements AttributeConverter<AAlgoritmoConsenso, String> {
    String algoritmoCol = "";
    @Override
    public String convertToDatabaseColumn(AAlgoritmoConsenso algoritmoConsenso) {
        if(algoritmoConsenso == null) return null;
        else if(algoritmoConsenso instanceof absoluta) algoritmoCol = "absoluta";
        else if(algoritmoConsenso instanceof mayoriaSimple) algoritmoCol = "mayoriaSimple";
        else if(algoritmoConsenso instanceof multiplesMenciones) algoritmoCol = "multiplesMenciones";

        return algoritmoCol;
    }

    @Override
    public AAlgoritmoConsenso convertToEntityAttribute(String s) {
        if(s == null) return null;

        AAlgoritmoConsenso algoritmoConsenso = null;

        switch(s){
            case "absoluta": algoritmoConsenso = new absoluta(); break;
            case "mayoriaSimple": algoritmoConsenso = new mayoriaSimple(); break;
            case "multiplesMenciones": algoritmoConsenso = new multiplesMenciones(); break;
        }

        return algoritmoConsenso;
    }

}
