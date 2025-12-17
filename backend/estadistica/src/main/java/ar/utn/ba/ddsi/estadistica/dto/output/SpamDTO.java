package ar.utn.ba.ddsi.estadistica.dto.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpamDTO {
  @JsonProperty("total_solicitudes")
  private Long totalSolicitudes;
  @JsonProperty("total_solicitudes_spam")
  private Long solicitudesSpam;
  @JsonProperty("porcentaje_spam")
  private Double porcentajeSpam;
}

