package ar.utn.ba.ddsi.proxy_metamapa.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ContenidoMultimediaDTO {
  @JsonProperty("path")
  private String pathArchivo;
  @JsonProperty("tipo")
  private String tipoContenido;
}
