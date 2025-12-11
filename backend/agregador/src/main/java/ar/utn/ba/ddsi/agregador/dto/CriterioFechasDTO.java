package ar.utn.ba.ddsi.agregador.dto;

import ar.utn.ba.ddsi.agregador.domain.coleccion.criterios.TipoFecha;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import lombok.Data;

@Data
public class CriterioFechasDTO implements CriterioDTO {
  @JsonProperty("fecha_inicial")
  private LocalDate fechaInicial;
  @JsonProperty("fecha_final")
  private LocalDate fechaFinal;
  @JsonProperty("tipo_fecha")
  private TipoFecha tipoFecha;
}
