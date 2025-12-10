package ar.utn.ba.ddsi.agregador.domain.fuente.config;

import ar.utn.ba.ddsi.agregador.domain.fuente.factory.TipoFuente;
import lombok.Data;

@Data
public class FuenteConfig {
  private String url;
  private TipoFuente tipo;
}
