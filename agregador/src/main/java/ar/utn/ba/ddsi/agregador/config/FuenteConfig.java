package ar.utn.ba.ddsi.agregador.config;

import ar.utn.ba.ddsi.agregador.infra.fuente.factory.TipoFuente;
import lombok.Data;

@Data
public class FuenteConfig {
  private String url;
  private TipoFuente tipo;
}
