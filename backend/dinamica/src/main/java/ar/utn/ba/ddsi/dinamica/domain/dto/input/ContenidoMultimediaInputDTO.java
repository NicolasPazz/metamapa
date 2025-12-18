package ar.utn.ba.ddsi.dinamica.domain.dto.input;

import ar.utn.ba.ddsi.dinamica.domain.hecho.ContenidoMultimedia;
import ar.utn.ba.ddsi.dinamica.domain.hecho.TipoContenido;
import ar.utn.ba.ddsi.dinamica.domain.solicitud.SolicitudHecho;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ContenidoMultimediaInputDTO {
  @NotBlank(message = "El path del archivo es obligatorio")
  @JsonProperty("path")
  private String pathArchivo;

  @NotNull(message = "El tipo de contenido es obligatorio")
  @JsonProperty("tipo")
  private TipoContenido tipoContenido;

  public ContenidoMultimedia toContenidoMultimedia(SolicitudHecho solicitud) {
    return new ContenidoMultimedia(this.pathArchivo, this.tipoContenido, solicitud);
  }
}
