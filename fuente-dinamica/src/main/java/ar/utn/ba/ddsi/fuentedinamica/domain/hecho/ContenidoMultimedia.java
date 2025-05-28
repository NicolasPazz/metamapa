package ar.utn.ba.ddsi.fuentedinamica.domain.hecho;

import ar.utn.ba.ddsi.fuentedinamica.domain.hecho.TipoContenido;
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
}
