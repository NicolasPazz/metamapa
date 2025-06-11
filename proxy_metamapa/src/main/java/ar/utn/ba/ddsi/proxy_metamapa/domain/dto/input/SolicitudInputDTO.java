package ar.utn.ba.ddsi.proxy_metamapa.domain.dto.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SolicitudInputDTO {
  @JsonProperty("id_hecho")
  private Long idHecho;
  @JsonProperty("texto_fundamentos")
  private String textoFundamentos;
  @JsonProperty("id_contribuyente")
  private Long idContribuyente;
}
