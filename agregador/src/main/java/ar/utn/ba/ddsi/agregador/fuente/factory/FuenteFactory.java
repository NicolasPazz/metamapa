package ar.utn.ba.ddsi.agregador.fuente.factory;

import ar.utn.ba.ddsi.agregador.config.FuenteConfig;
import ar.utn.ba.ddsi.agregador.fuente.IFuente;
import ar.utn.ba.ddsi.agregador.fuente.impl.FuenteCron;
import ar.utn.ba.ddsi.agregador.fuente.impl.FuenteTiempoReal;
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
