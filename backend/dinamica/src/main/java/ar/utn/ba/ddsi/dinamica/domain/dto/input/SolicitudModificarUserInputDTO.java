package ar.utn.ba.ddsi.dinamica.domain.dto.input;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class SolicitudModificarUserInputDTO {
  @JsonProperty("id_contribuyente")
  private Long idContribuyente;
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
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
  @JsonProperty("fecha_acontecimiento")
  private String nuevoFechaDeAcontecimiento;
  @JsonProperty("multimedia")
  private List<ContenidoMultimediaInputDTO> nuevoContenidoMultimedia;
}
