package ar.utn.ba.ddsi.agregador.dto;

import ar.utn.ba.ddsi.agregador.domain.hecho.OrigenHecho;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CriterioOrigenDTO implements CriterioDTO {
  private OrigenHecho origen;
}
