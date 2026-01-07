package ar.utn.ba.ddsi.frontend.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ContenidoMultimediaDTO {
  @JsonProperty("path")
  private String pathArchivo;
  @JsonProperty("tipo")
  private String tipoContenido;
}
