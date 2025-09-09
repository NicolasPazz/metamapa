package ar.utn.ba.ddsi.agregador.domain.dto.input;

import ar.utn.ba.ddsi.agregador.domain.coleccion.criterios.Criterio;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ModificarCriterioColeccionDTO {
  @JsonProperty("criterio")
  private Criterio criterio;
}