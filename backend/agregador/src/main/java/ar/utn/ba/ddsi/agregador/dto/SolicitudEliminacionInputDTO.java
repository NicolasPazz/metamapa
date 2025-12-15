package ar.utn.ba.ddsi.agregador.dto;

import ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.EstadoSolicitud;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class SolicitudEliminacionInputDTO {
  private Long id;
  @JsonProperty("hecho_id")
  private Long hechoId;
  @JsonProperty("texto_fundamentos")
  private String textoFundamentos;
  private LocalDate fecha;
  private EstadoSolicitud estado;
  @JsonProperty("contribuyente_id")
  private Long contribuyenteId;
  @JsonProperty("fuente_id")
  private Long fuenteId;
  @JsonProperty("id_origen")
  private Long idOrigen;
}
