package ar.utn.ba.ddsi.agregador.domain.fuente.config;

import ar.utn.ba.ddsi.agregador.domain.fuente.factory.TipoFuente;
import lombok.Data;

@Data
public class FuenteConfig {
  private Long id;
  private String nombre;
  private String url;
  private TipoFuente tipo;
}
