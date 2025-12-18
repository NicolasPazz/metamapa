package ar.utn.ba.ddsi.agregador.dto;

import ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.EstadoSolicitud;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class SolicitudEliminacionDTO {
  private Long id;
  private HechoDTO hecho;
  @JsonProperty("texto_fundamentos")
  private String textoFundamentos;
  private LocalDateTime fecha;
  private EstadoSolicitud estado;
  @JsonProperty("contribuyente_id")
  private Long contribuyenteId;
  @JsonProperty("es_spam")
  private Boolean spam;
  private List<TransicionEstadoSolicitudDTO> transiciones;
  @JsonProperty("fuente_id")
  private Long fuenteId;
  @JsonProperty("id_origen")
  private Long idOrigen;
}
