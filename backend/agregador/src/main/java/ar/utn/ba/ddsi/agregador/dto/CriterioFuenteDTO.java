package ar.utn.ba.ddsi.agregador.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;
import lombok.Data;

@Data
public class CriterioFuenteDTO implements CriterioDTO {
  private Set<Long> fuentes;
  
  @JsonProperty("nombres_fuentes")
  private Set<String> nombresFuentes;
}
