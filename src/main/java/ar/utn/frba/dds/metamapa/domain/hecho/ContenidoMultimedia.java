package ar.utn.frba.dds.metamapa.domain.hecho;

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
