package ar.utn.ba.ddsi.agregador.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "tipo"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = CriterioFechasDTO.class, name = "fecha"),
    @JsonSubTypes.Type(value = CriterioLugarDTO.class, name = "lugar"),
    @JsonSubTypes.Type(value = CriterioIgualdadDTO.class, name = "igualdad"),
    @JsonSubTypes.Type(value = CriterioOrigenDTO.class, name = "origen"),
    @JsonSubTypes.Type(value = CriterioFuenteDTO.class, name = "fuente")
})
public interface CriterioDTO {
}
