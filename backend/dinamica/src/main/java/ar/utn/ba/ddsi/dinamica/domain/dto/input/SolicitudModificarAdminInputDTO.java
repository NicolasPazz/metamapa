package ar.utn.ba.ddsi.dinamica.domain.dto.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class SolicitudModificarAdminInputDTO {
  @JsonProperty("estado_solicitud")
  private String nuevoEstadoSolicitud;
  @JsonProperty("titulo")
  private String nuevoTitulo;
  @JsonProperty("descripcion")
  private String nuevoDescripcion;
  @JsonProperty("categoria")
  private String nuevoNombreCategoria;
  @JsonProperty("latitud")
  private Double nuevoLatitud;
  @JsonProperty("longitud")
  private Double nuevoLongitud;
  @JsonProperty("fecha_de_acontecimiento")
  private String nuevoFechaDeAcontecimiento;
  @JsonProperty("multimedia")
  private List<ContenidoMultimediaInputDTO> nuevoContenidoMultimedia;
}
