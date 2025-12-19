package ar.utn.ba.ddsi.dinamica.domain.dto.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HechoAgregadorDTO {
  @JsonProperty("id")
  private Long id;
  
  @JsonProperty("fuente_id")
  private Long fuenteId;
  
  @JsonProperty("id_origen")
  private Long idOrigen;
}

