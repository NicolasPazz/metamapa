package ar.utn.ba.ddsi.agregador.converter;

import ar.utn.ba.ddsi.agregador.domain.coleccion.criterios.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class CriterioAttributeConverter implements AttributeConverter<Criterio, String> {
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    public String convertToDatabaseColumn(Criterio criterio) {
        if(criterio == null) return null;
        
        try {
            return objectMapper.writeValueAsString(criterio);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializando criterio", e);
        }
    }

    @Override
    public Criterio convertToEntityAttribute(String json) {
        if(json == null || json.trim().isEmpty()) return null;

        try {
            // Intentar parsear como JSON completo primero
            JsonNode jsonNode = objectMapper.readTree(json);
            String tipo = jsonNode.get("tipo").asText();
            
            switch(tipo) {
                case "fechas":
                    return objectMapper.treeToValue(jsonNode, CriterioFechas.class);
                case "fuente":
                    return objectMapper.treeToValue(jsonNode, CriterioFuente.class);
                case "igualdad":
                    return objectMapper.treeToValue(jsonNode, CriterioIgualdad.class);
                case "lugar":
                    return objectMapper.treeToValue(jsonNode, CriterioLugar.class);
                case "origen":
                    return objectMapper.treeToValue(jsonNode, CriterioOrigen.class);
                case "combinado":
                    return objectMapper.treeToValue(jsonNode, CriterioCombinado.class);
                default:
                    throw new IllegalArgumentException("Tipo de criterio desconocido: " + tipo);
            }
        } catch (JsonProcessingException e) {
            // Si falla el parseo JSON, intentar con solo tipo
            try {
                String tipo = json.trim();
                switch(tipo) {
                    case "fechas":
                        return new CriterioFechas();
                    case "fuente":
                        return new CriterioFuente();
                    case "igualdad":
                        return new CriterioIgualdad();
                    case "lugar":
                        return new CriterioLugar();
                    case "origen":
                        return new CriterioOrigen();
                    case "combinado":
                        return new CriterioCombinado();
                    default:
                        throw new IllegalArgumentException("Tipo de criterio desconocido: " + tipo);
                }
            } catch (Exception ex) {
                throw new RuntimeException("Error deserializando criterio: " + json, ex);
            }
        }
    }
}
