package ar.utn.ba.ddsi.agregador.repositorie.converter;

import ar.utn.ba.ddsi.agregador.domain.coleccion.criterios.Criterio;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.ArrayList;
import java.util.List;

@Converter
public class CriterioListAttributeConverter implements AttributeConverter<List<Criterio>, String> {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
      .registerModule(new JavaTimeModule())
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

  private static final TypeReference<List<Criterio>> TYPE_REFERENCE = new TypeReference<>() {};

  @Override
  public String convertToDatabaseColumn(List<Criterio> criterios) {
    if (criterios == null || criterios.isEmpty()) {
      return null;
    }

    try {
      return OBJECT_MAPPER.writerFor(TYPE_REFERENCE).writeValueAsString(criterios);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Error serializando lista de criterios a JSON", e);
    }
  }

  @Override
  public List<Criterio> convertToEntityAttribute(String json) {
    if (json == null || json.isBlank()) {
      return new ArrayList<>();
    }

    try {
      return OBJECT_MAPPER.readValue(json, TYPE_REFERENCE);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Error deserializando JSON a lista de criterios", e);
    }
  }
}

