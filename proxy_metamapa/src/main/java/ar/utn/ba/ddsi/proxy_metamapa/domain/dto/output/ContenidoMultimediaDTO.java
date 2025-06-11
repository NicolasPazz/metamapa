package ar.utn.ba.ddsi.proxy_metamapa.domain.dto.output;

import lombok.Data;

@Data
public class ContenidoMultimediaDTO {
  private String pathArchivo;
  private String tipoContenido;

  public ContenidoMultimediaDTO(String pathArchivo, String tipoContenido) {
    this.pathArchivo = pathArchivo;
    this.tipoContenido = tipoContenido;
  }
}
