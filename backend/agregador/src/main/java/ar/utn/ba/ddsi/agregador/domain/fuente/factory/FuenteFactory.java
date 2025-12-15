package ar.utn.ba.ddsi.agregador.domain.fuente.factory;

import ar.utn.ba.ddsi.agregador.domain.fuente.config.FuenteConfig;
import ar.utn.ba.ddsi.agregador.domain.fuente.AFuente;
import ar.utn.ba.ddsi.agregador.domain.fuente.impl.FuenteCron;
import ar.utn.ba.ddsi.agregador.domain.fuente.impl.FuenteTiempoReal;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class FuenteFactory {
  public AFuente crearFuente(FuenteConfig config) {

    WebClient webClient = WebClient.builder()
        .baseUrl(config.getUrl())
        .build();

    Long idAsignado = config.getId();
    String nombre = config.getNombre() != null ? config.getNombre() : "Fuente " + idAsignado;

    AFuente fuente = switch (config.getTipo()) {
      case CRON -> new FuenteCron(idAsignado, nombre, webClient);
      case TIEMPO_REAL -> new FuenteTiempoReal(idAsignado, nombre, webClient);
    };

    log.info("Fuente {} creada: {} (ID: {}, URL: {})",
        config.getTipo(), nombre, idAsignado, config.getUrl());

    return fuente;
  }
}
