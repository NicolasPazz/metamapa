package ar.utn.ba.ddsi.agregador.domain.fuente.factory;

import ar.utn.ba.ddsi.agregador.domain.fuente.AFuente;
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
      // Crear y persistir fuente concreta (ya incluye persistencia JPA)
      AFuente fuente = factory.crearFuente(config);

      // Persistir en BD
      fuentesRepository.save(fuente);
      log.info("💾 Fuente persistida en BD: {} (ID: {})", fuente.getNombre(), fuente.getId());

      // Agregar fuente al servicio
      fuentesService.agregarFuente(fuente);
      log.info("🔌 Fuente cargada: {} - {} ({})", config.getNombre(), config.getUrl(), config.getTipo());
    });
  }
}
