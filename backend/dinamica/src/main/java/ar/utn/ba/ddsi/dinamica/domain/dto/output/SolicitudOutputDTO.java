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
  private Long id;
  @JsonProperty("contribuyente_id")
  private Long idContribuyente;
  private String titulo;
  private String descripcion;
  @JsonProperty("categoria")
  private String nombreCategoria;
  private Double latitud;
  private Double longitud;
  @JsonProperty("fecha_acontecimiento")
  private LocalDateTime fechaDeAcontecimiento;
  @JsonProperty("multimedia")
  private List<ContenidoMultimediaOutputDTO> contenidoMultimedia;
  @JsonProperty("fecha_carga")
  private LocalDateTime fechaDeCargaSolicitud;
  private EstadoSolicitud estado;
  @JsonProperty("transiciones")
  private List<TransicionEstadoOutputDTO> transiciones;
  @JsonProperty("hecho_id")
  private Long idHecho;
  @JsonProperty("hecho_agregador_id")
  private Long idHechoAgregador;
  @JsonProperty("hecho_enviado")
  private Boolean hechoEnviado;
}
