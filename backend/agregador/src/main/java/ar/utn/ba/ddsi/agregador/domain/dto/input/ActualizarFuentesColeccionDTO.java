package ar.utn.ba.ddsi.agregador.domain.dto.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ActualizarFuentesColeccionDTO {
  @JsonProperty("id_fuente")
  private Long idFuente;

  @JsonProperty("accion")
  private String accion; // valores posibles: AGREGAR o ELIMINAR
}
