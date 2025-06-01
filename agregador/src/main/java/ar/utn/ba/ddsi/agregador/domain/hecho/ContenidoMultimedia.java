package ar.utn.ba.ddsi.agregador.domain.hecho;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ContenidoMultimedia {
  private Long id;
  private String pathArchivo;
  private TipoContenido tipoContenido;

  public ContenidoMultimedia(String pathArchivo, TipoContenido tipoContenido) {
    this.pathArchivo = pathArchivo;
    this.tipoContenido = tipoContenido;
  }
}
