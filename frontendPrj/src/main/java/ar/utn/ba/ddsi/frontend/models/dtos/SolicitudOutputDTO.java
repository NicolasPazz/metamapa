package ar.utn.ba.ddsi.frontend.models.dtos;

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
