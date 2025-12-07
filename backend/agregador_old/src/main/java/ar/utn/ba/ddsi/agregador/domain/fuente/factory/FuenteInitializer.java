package ar.utn.ba.ddsi.agregador.domain.fuente.factory;

import ar.utn.ba.ddsi.agregador.config.FuentesProperties;
import ar.utn.ba.ddsi.agregador.service.impl.FuentesService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class FuenteInitializer {

  private final FuentesProperties properties;
  private final FuenteFactory factory;
  private final FuentesService fuentesService;

  @PostConstruct
  public void init() {
    properties.getConfig().forEach(config -> {
      fuentesService.agregarFuente(factory.crearFuente(config));
      log.info("🔌 Fuente cargada: " + config.getUrl() + " (" + config.getTipo() + ")");
    });
  }
}