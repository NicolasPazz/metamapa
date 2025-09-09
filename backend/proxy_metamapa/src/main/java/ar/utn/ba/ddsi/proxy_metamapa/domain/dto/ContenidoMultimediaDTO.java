package ar.utn.ba.ddsi.proxy_metamapa.domain.dto;

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
