package ar.utn.ba.ddsi.agregador.domain.dto.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ModificarCriterioColeccionDTO {
  @JsonProperty("criterio")
  private CriterioInputDTO criterio;
}