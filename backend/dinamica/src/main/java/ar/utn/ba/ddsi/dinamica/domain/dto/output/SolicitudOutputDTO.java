package ar.utn.ba.ddsi.dinamica.domain.dto.output;

import ar.utn.ba.ddsi.dinamica.domain.solicitud.EstadoSolicitud;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SolicitudOutputDTO {
  @JsonProperty("id")
  private Long id;
  @JsonProperty("contribuyente_id")
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
  @JsonProperty("fecha_acontecimiento")
  private LocalDateTime fechaDeAcontecimiento;
  @JsonProperty("multimedia")
  private List<ContenidoMultimediaOutputDTO> contenidoMultimedia;
  @JsonProperty("fecha_carga")
  private LocalDateTime fechaDeCargaSolicitud;
  @JsonProperty("estado")
  private EstadoSolicitud estado;
  @JsonProperty("transiciones")
  private List<TransicionEstadoOutputDTO> transiciones;
}
