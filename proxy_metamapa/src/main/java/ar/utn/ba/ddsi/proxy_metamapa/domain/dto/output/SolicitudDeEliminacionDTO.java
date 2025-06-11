package ar.utn.ba.ddsi.proxy_metamapa.domain.dto.output;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SolicitudDeEliminacionDTO {
  @JsonProperty("id_hecho")
  private Long idHecho;
  @JsonProperty("texto_fundamentos")
  private String textoFundamentos;
  @JsonProperty("id_contribuyente")
  private Long idContribuyente;
}
