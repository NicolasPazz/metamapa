package ar.utn.ba.ddsi.agregador.domain.dto.input;

import ar.utn.ba.ddsi.agregador.infra.fuente.factory.TipoFuente;
import lombok.Data;

@Data
public class FuenteConfigDTO {
  private String url;
  private TipoFuente tipo;
}
