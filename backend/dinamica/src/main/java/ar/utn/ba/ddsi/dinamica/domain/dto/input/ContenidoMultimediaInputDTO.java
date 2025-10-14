package ar.utn.ba.ddsi.dinamica.domain.dto.input;

import ar.utn.ba.ddsi.dinamica.domain.hecho.ContenidoMultimedia;
import ar.utn.ba.ddsi.dinamica.domain.hecho.TipoContenido;
import ar.utn.ba.ddsi.dinamica.domain.solicitud.SolicitudHecho;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ContenidoMultimediaInputDTO {
  String pathArchivo;
  String tipoContenido;

  public ContenidoMultimedia toContenidoMultimedia(SolicitudHecho solicitud) {
    return new ContenidoMultimedia(this.pathArchivo, TipoContenido.valueOf(this.tipoContenido), solicitud);
  }
}
