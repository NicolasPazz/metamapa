package ar.utn.ba.ddsi.dinamica.domain.dto.output;

import ar.utn.ba.ddsi.dinamica.domain.hecho.TipoContenido;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ContenidoMultimediaOutputDTO {
  @JsonProperty("path")
  private String pathArchivo;
  @JsonProperty("tipo")
  private TipoContenido tipoContenido;
}
