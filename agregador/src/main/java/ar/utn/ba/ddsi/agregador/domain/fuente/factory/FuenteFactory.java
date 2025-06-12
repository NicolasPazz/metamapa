package ar.utn.ba.ddsi.agregador.domain.fuente.factory;

import ar.utn.ba.ddsi.agregador.config.FuenteConfig;
import ar.utn.ba.ddsi.agregador.domain.fuente.IFuente;
import ar.utn.ba.ddsi.agregador.domain.fuente.impl.FuenteCron;
import ar.utn.ba.ddsi.agregador.domain.fuente.impl.FuenteTiempoReal;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class FuenteFactory {

  public IFuente crearFuente(FuenteConfig config) {
    WebClient webClient = WebClient.builder().baseUrl(config.getUrl()).build();
    return switch (config.getTipo()) {
      case CRON -> new FuenteCron(webClient);
      case TIEMPO_REAL -> new FuenteTiempoReal(webClient);
    };
  }
}
