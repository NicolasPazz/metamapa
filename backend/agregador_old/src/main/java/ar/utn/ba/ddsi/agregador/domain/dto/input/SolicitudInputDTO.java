package ar.utn.ba.ddsi.agregador.domain.dto.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SolicitudInputDTO {
  @JsonProperty("idHecho")
  private Long idHecho;
  @JsonProperty("textoFundamentos")
  private String textoFundamentos;
  @JsonProperty("idContribuyente")
  private Long idContribuyente;
}
