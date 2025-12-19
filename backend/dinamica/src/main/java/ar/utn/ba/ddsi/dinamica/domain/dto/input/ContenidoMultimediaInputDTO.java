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
  @JsonProperty("path")
  private String pathArchivo;

  @NotNull(message = "El tipo de contenido es obligatorio")
  @JsonProperty("tipo")
  private TipoContenido tipoContenido;

  @JsonProperty("contenido_base64")
  private String contenidoBase64;

  public ContenidoMultimedia toContenidoMultimedia(SolicitudHecho solicitud) {
    // Si no hay path pero hay contenido_base64, el path ya fue establecido al guardar el archivo
    if (this.pathArchivo == null || this.pathArchivo.isEmpty()) {
      throw new IllegalStateException("El path del archivo es obligatorio");
    }
    return new ContenidoMultimedia(this.pathArchivo, this.tipoContenido, solicitud);
  }
}
