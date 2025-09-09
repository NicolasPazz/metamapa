package ar.utn.ba.ddsi.proxy_metamapa.domain.dto.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import lombok.Data;

@Data
public class SolicitudOutputDTO {
  private Long id;
  @JsonProperty("id_hecho")
  private Long idHecho;
  @JsonProperty("texto_fundamentos")
  private String textoFundamentos;
  private LocalDate fecha;
  private String estado;
  @JsonProperty("id_contribuyente")
  private Long idContribuyente;
}
