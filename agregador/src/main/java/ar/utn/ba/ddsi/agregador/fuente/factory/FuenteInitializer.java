package ar.utn.ba.ddsi.agregador.fuente.factory;

import ar.utn.ba.ddsi.agregador.config.FuentesProperties;
import ar.utn.ba.ddsi.agregador.fuente.IFuente;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class FuenteInitializer {

  private final FuentesProperties properties;
  private final FuenteFactory factory;

  @Getter
  private final List<IFuente> fuentes = new ArrayList<>();

  public FuenteInitializer(FuentesProperties properties, FuenteFactory factory) {
    this.properties = properties;
    this.factory = factory;
  }

  @PostConstruct
  public void init() {
    for (var config : properties.getConfig()) {
      fuentes.add(factory.crearFuente(config));
    }
  }
}