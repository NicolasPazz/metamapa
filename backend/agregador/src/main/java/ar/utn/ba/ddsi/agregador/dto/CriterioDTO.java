package ar.utn.ba.ddsi.agregador.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.DEDUCTION
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = CriterioIgualdadDTO.class),
    @JsonSubTypes.Type(value = CriterioLugarDTO.class),
    @JsonSubTypes.Type(value = CriterioFechasDTO.class),
    @JsonSubTypes.Type(value = CriterioOrigenDTO.class),
    @JsonSubTypes.Type(value = CriterioFuenteDTO.class)
})
public interface CriterioDTO {
}
