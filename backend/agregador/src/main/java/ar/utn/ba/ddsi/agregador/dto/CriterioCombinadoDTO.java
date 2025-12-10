package ar.utn.ba.ddsi.agregador.dto;

import java.util.List;
import lombok.Data;

@Data
public class CriterioCombinadoDTO implements CriterioDTO {
  private List<CriterioDTO> criterios;
}
