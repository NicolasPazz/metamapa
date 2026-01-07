package ar.utn.ba.ddsi.agregador.schedulers;

import ar.utn.ba.ddsi.agregador.service.AgregadorService;
import ar.utn.ba.ddsi.agregador.service.ColeccionesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class AgregadorScheduler {

    private final AgregadorService agregadorService;
    private final ColeccionesService coleccionesService;

    private static final int MAX_RETRIES = 5;
    private static final long INITIAL_DELAY_SECONDS = 30; // Esperar 30 segundos antes del primer intento
    private static final long RETRY_DELAY_SECONDS = 15; // Esperar 15 segundos entre reintentos

    @EventListener(ApplicationReadyEvent.class)
    @Async
    public void onStartup() {
        log.info("[Scheduler] Iniciando proceso de actualización de hechos y colecciones en startup");
        
        // Esperar un tiempo inicial para que las fuentes estén completamente listas
        try {
            log.info("[Scheduler] Esperando {} segundos antes del primer intento para que las fuentes estén listas...", INITIAL_DELAY_SECONDS);
            Thread.sleep(TimeUnit.SECONDS.toMillis(INITIAL_DELAY_SECONDS));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("[Scheduler] Interrupción durante el delay inicial");
        }

        // Intentar actualizar hechos con retry logic
        boolean hechosActualizados = actualizarHechosConRetry();
        
        if (hechosActualizados) {
            log.info("[Scheduler] Hechos actualizados exitosamente, procediendo con colecciones");
            actualizarColeccionesSegunConsenso();
        } else {
            log.warn("[Scheduler] No se pudieron actualizar hechos después de {} intentos. Las colecciones se actualizarán en el próximo ciclo programado.", MAX_RETRIES);
        }
    }

    private boolean actualizarHechosConRetry() {
        for (int intento = 1; intento <= MAX_RETRIES; intento++) {
            try {
                log.info("[Scheduler] Intento {}/{} de actualizar hechos", intento, MAX_RETRIES);
                agregadorService.actualizarHechos();
                log.info("[Scheduler] Hechos actualizados exitosamente en el intento {}", intento);
                return true;
            } catch (Exception e) {
                log.warn("[Scheduler] Error en intento {}/{} de actualizar hechos: {}", 
                    intento, MAX_RETRIES, e.getMessage());
                
                if (intento < MAX_RETRIES) {
                    try {
                        log.info("[Scheduler] Esperando {} segundos antes del siguiente intento...", RETRY_DELAY_SECONDS);
                        Thread.sleep(TimeUnit.SECONDS.toMillis(RETRY_DELAY_SECONDS));
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error("[Scheduler] Interrupción durante el retry delay", ie);
                        return false;
                    }
                } else {
                    log.error("[Scheduler] Error en startup actualizando hechos después de {} intentos", MAX_RETRIES, e);
                }
            }
        }
        return false;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void refrescarHechosYColecciones() {
        try {
            agregadorService.actualizarHechos();
        } catch (Exception e) {
            log.error("[Scheduler] Error al actualizar hechos", e);
        }
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void actualizarColeccionesSegunConsenso() {
        try {
            coleccionesService.actualizarHechosSegunConsenso();
        } catch (Exception e) {
            log.error("[Scheduler] Error al actualizar consenso", e);
        }
    }
}