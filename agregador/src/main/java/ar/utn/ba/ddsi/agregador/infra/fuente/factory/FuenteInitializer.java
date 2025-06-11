package ar.utn.ba.ddsi.agregador.infra.fuente.factory;

import ar.utn.ba.ddsi.agregador.config.FuentesProperties;
import ar.utn.ba.ddsi.agregador.service.impl.FuentesService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FuenteInitializer {

  private final FuentesProperties properties;
  private final FuenteFactory factory;
  private final FuentesService fuentesService;

  @PostConstruct
  public void init() {
    properties.getConfig().forEach(config -> {
      fuentesService.agregarFuente(factory.crearFuente(config));
      System.out.println("🔌 Fuente cargada: " + config.getUrl() + " (" + config.getTipo() + ")");
    });
  }
}