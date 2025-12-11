package ar.utn.ba.ddsi.agregador.dto;

import ar.utn.ba.ddsi.agregador.domain.coleccion.criterios.TipoDato;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CriterioIgualdadDTO implements CriterioDTO {
  @JsonProperty("cadena_a_comparar")
  private String cadenaAComparar;
  @JsonProperty("tipo_de_dato")
  private TipoDato tipoDeDato;
}