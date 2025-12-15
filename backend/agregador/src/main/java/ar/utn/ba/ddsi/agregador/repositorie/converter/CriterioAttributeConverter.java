package ar.utn.ba.ddsi.agregador.repositorie.converter;

import ar.utn.ba.ddsi.agregador.domain.coleccion.criterios.Criterio;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class CriterioAttributeConverter implements AttributeConverter<Criterio, String> {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(Criterio criterio) {
    if (criterio == null) return null;
    try {
      return objectMapper.writeValueAsString(criterio);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Error serializando criterio", e);
    }
  }

  @Override
  public Criterio convertToEntityAttribute(String json) {
    if (json == null || json.isBlank()) return null;

    try {
      return objectMapper.readValue(json, Criterio.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Error deserializando criterio: " + json, e);
    }
  }
}
