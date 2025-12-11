package ar.utn.ba.ddsi.agregador.dto;

import ar.utn.ba.ddsi.agregador.domain.hecho.multimedia.TipoContenido;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ContenidoMultimediaDTO {
  @JsonProperty("path")
  private String pathArchivo;
  @JsonProperty("tipo_contenido")
  private TipoContenido tipoContenido;
}
