package ar.utn.ba.ddsi.dinamica.domain.dto.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SolicitudOutputDTO {
  @JsonProperty("id_solicitud")
  private Long id;
  @JsonProperty("id_contribuyente")
  private Long idContribuyente;
  @JsonProperty("titulo")
  private String titulo;
  @JsonProperty("descripcion")
  private String descripcion;
  @JsonProperty("categoria")
  private String nombreCategoria;
  @JsonProperty("latitud")
  private Double latitud;
  @JsonProperty("longitud")
  private Double longitud;
  @JsonProperty("fecha_de_acontecimiento")
  private String fechaDeAcontecimiento;
  @JsonProperty("multimedia")
  private List<ContenidoMultimediaOutputDTO> contenidoMultimedia;
  @JsonProperty("fecha_de_carga_solicitud")
  private String fechaDeCargaSolicitud;
  @JsonProperty("estado")
  private String estado;
}
