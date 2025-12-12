package ar.utn.ba.ddsi.agregador.domain.fuente.factory;

import ar.utn.ba.ddsi.agregador.domain.fuente.Fuente;
import ar.utn.ba.ddsi.agregador.domain.fuente.config.FuentesProperties;
import ar.utn.ba.ddsi.agregador.repositorie.IFuentesRepository;
import ar.utn.ba.ddsi.agregador.service.FuentesService;
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
  private final IFuentesRepository fuentesRepository;

  @PostConstruct
  public void init() {
    properties.getConfig().forEach(config -> {
      // Persistir información de fuente en BD
      Fuente fuenteEntity = Fuente.builder()
          .id(config.getId())
          .nombre(config.getNombre() != null ? config.getNombre() : "Fuente " + config.getId())
          .url(config.getUrl())
          .tipo(config.getTipo())
          .activa(true)
          .build();

      fuentesRepository.save(fuenteEntity);
      log.info("💾 Fuente persistida en BD: {} (ID: {})", fuenteEntity.getNombre(), fuenteEntity.getId());

      // Agregar fuente al servicio
      fuentesService.agregarFuente(factory.crearFuente(config));
      log.info("🔌 Fuente cargada: {} - {} ({})", config.getNombre(), config.getUrl(), config.getTipo());
    });
  }
}