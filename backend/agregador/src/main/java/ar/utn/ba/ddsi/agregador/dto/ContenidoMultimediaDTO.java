package ar.utn.ba.ddsi.agregador.dto;

import ar.utn.ba.ddsi.agregador.domain.hecho.multimedia.TipoContenido;
import lombok.Data;

@Data
public class ContenidoMultimediaDTO {
  private String pathArchivo;
  private TipoContenido tipoContenido;
}
