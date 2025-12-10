package ar.utn.ba.ddsi.agregador.domain.dto.output;

import ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.EstadoSolicitud;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDate;

@Data
public class SolicitudOutputDTO {
  private Long id;
  @JsonProperty("id_hecho")
  private Long idHecho;
  @JsonProperty("texto_fundamentos")
  private String textoFundamentos;
  private LocalDate fecha;
  private EstadoSolicitud estado;
  @JsonProperty("id_contribuyente")
  private Long idContribuyente;
  private Boolean spam;
}
