package ar.utn.ba.ddsi.agregador.schedulers;

import ar.utn.ba.ddsi.agregador.service.impl.AgregadorService;
import ar.utn.ba.ddsi.agregador.service.impl.ColeccionesService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class agregadorScheduler {

    private final AgregadorService agregadorService;
    private final ColeccionesService coleccionesService;

    @PostConstruct
    public void onStartup() {
        log.info("⌛ onStartup: Scheduler de agregador iniciado, esto puede tardar hasta dos minutos");

        try {
            agregadorService.actualizarHechos();
            log.info("✅ onStartup: Scheduler de agregador finalizado, hechos actualizados correctamente");
        } catch (Exception e) {
            log.error("❌ Error en onStartup al actualizar hechos", e);
        }

        this.actualizarColeccionesSegunConsenso();
    }

    @Scheduled(cron = "0 0 * * * *")
    public void refrescarHechosYColecciones() {
        new Thread(() -> {
            try {
                log.info("⌛ [Scheduler] Iniciando actualización de hechos en hilo separado...");
                agregadorService.actualizarHechos();
                log.info("✅ [Scheduler] Actualización de hechos finalizada correctamente.");
            } catch (Exception e) {
                log.error("❌ [Scheduler] Error al actualizar hechos", e);
            }
        }, "cron-refrescarHechosYColecciones").start();
    }

    /**
     * Cron Job que actualiza los hechos consensuados de todas las colecciones
     * Se ejecuta todos los días a las 3:00 AM
     * Formato cron: "0 0 3 * * *" = segundo minuto hora día mes día_semana
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void actualizarColeccionesSegunConsenso() {
        new Thread(() -> {
            try {
                log.info("⌛ [Scheduler] Iniciando actualización de hechos consensuados de todas las colecciones...");
                long startTime = System.currentTimeMillis();
                
                coleccionesService.actualizarHechosSegunConsenso();
                
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                
                log.info("✅ [Scheduler] Actualización de hechos consensuados finalizada correctamente en {} ms", duration);
            } catch (Exception e) {
                log.error("❌ [Scheduler] Error al actualizar hechos consensuados de las colecciones", e);
            }
        }, "cron-actualizarColeccionesSegunConsenso").start();
    }
}