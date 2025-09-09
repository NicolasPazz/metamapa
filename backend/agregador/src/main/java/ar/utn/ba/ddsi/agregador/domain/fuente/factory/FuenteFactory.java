package ar.utn.ba.ddsi.agregador.domain.fuente.factory;

import ar.utn.ba.ddsi.agregador.config.FuenteConfig;
import ar.utn.ba.ddsi.agregador.domain.fuente.IFuente;
import ar.utn.ba.ddsi.agregador.domain.fuente.impl.FuenteCron;
import ar.utn.ba.ddsi.agregador.domain.fuente.impl.FuenteTiempoReal;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class FuenteFactory {

  private static final AtomicLong contadorId = new AtomicLong(1);

  public IFuente crearFuente(FuenteConfig config) {
    WebClient webClient = WebClient.builder().baseUrl(config.getUrl()).build();
    Long idAsignado = contadorId.getAndIncrement();
    IFuente fuente = switch (config.getTipo()) {
      case CRON -> new FuenteCron(webClient, idAsignado);
      case TIEMPO_REAL -> new FuenteTiempoReal(webClient, idAsignado);
    };
    log.info("Fuente {} creada con ID: {} y URL: {}", config.getTipo(), idAsignado, config.getUrl());

    return fuente;
  }
}
