package ar.utn.ba.ddsi.agregador.domain.dto;

import ar.utn.ba.ddsi.agregador.domain.hecho.OrigenHecho;
import lombok.Data;

@Data
public class CriterioOrigenDTO implements CriterioDTO {
  private OrigenHecho origen;
}
