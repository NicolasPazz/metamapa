package ar.utn.ba.ddsi.proxy_metamapa.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ContenidoMultimediaDTO {
  @JsonProperty("path")
  private String pathArchivo;
  @JsonProperty("tipo")
  private String tipoContenido;

  public ContenidoMultimediaDTO(String pathArchivo, String tipoContenido) {
    this.pathArchivo = pathArchivo;
    this.tipoContenido = tipoContenido;
  }
}
