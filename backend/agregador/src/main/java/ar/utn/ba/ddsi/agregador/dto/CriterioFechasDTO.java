package ar.utn.ba.ddsi.agregador.dto;

import ar.utn.ba.ddsi.agregador.domain.coleccion.criterios.TipoFecha;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CriterioFechasDTO implements CriterioDTO {
  @JsonProperty("fecha_inicial")
  private LocalDateTime fechaInicial;
  @JsonProperty("fecha_final")
  private LocalDateTime fechaFinal;
  @JsonProperty("tipo_fecha")
  private TipoFecha tipoFecha;
}
