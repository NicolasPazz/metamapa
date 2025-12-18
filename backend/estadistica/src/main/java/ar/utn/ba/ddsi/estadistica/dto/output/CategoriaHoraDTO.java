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
public class CategoriaHoraDTO {
  @JsonProperty("categoria")
  private String categoriaNombre;
  @JsonProperty("hora")
  private Integer hora;
  @JsonProperty("cantidad_hechos")
  private Long cantidadHechos;
}

