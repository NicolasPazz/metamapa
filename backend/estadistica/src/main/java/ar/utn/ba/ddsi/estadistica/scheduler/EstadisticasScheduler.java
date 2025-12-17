package ar.utn.ba.ddsi.estadistica.scheduler;

import ar.utn.ba.ddsi.estadistica.service.EstadisticasService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
public class EstadisticasScheduler {

  private final EstadisticasService estadisticasService;

  @PostConstruct
  public void onStartup() {

    log.info("⌛ [EstadisticasScheduler] onStartup: iniciando recalculo de estadísticas (puede tardar varios minutos)...");
    try {
      estadisticasService.recalcularTodasLasEstadisticas();
      log.info("✅ [EstadisticasScheduler] onStartup: estadísticas recalculadas.");
    } catch (Exception e) {
      log.error("❌ [EstadisticasScheduler] onStartup: error recalculando estadísticas", e);
    }
  }

  @Scheduled(cron = "0 0 3 * * ?")
  public void recalcularEstadisticasNocturnas() {
    log.info("🌙 [EstadisticasScheduler] ===== INICIANDO RECALCULO NOCTURNO DE ESTADÍSTICAS =====");
    log.info("🌙 [EstadisticasScheduler] Hora de ejecución: {}", java.time.LocalDateTime.now());

    try {
      estadisticasService.recalcularTodasLasEstadisticas();
      log.info("✅ [EstadisticasScheduler] ===== RECALCULO NOCTURNO COMPLETADO EXITOSAMENTE =====");
    } catch (Exception e) {
      log.error("❌ [EstadisticasScheduler] Error en recalculo nocturno: {}", e.getMessage(), e);
    }
  }
}

