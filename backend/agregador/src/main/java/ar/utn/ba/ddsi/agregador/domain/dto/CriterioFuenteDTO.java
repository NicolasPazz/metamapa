package ar.utn.ba.ddsi.agregador.domain.dto;

import java.util.Set;
import lombok.Data;

@Data
public class CriterioFuenteDTO implements CriterioDTO {
  private Set<Long> fuentesExcluidas;
}
