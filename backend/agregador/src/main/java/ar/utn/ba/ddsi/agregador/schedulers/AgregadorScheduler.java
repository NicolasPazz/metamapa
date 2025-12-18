package ar.utn.ba.ddsi.agregador.schedulers;

import ar.utn.ba.ddsi.agregador.service.AgregadorService;
import ar.utn.ba.ddsi.agregador.service.ColeccionesService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AgregadorScheduler {

    private final AgregadorService agregadorService;
    private final ColeccionesService coleccionesService;


    @PostConstruct
    public void onStartup() {
        log.info("⌛ onStartup: iniciando actualización de hechos (puede tardar ~2 min)...");
        try {
            agregadorService.actualizarHechos();
            log.info("✅ onStartup: hechos actualizados.");
        } catch (Exception e) {
            log.error("❌ onStartup: error actualizando hechos", e);
        }

        actualizarColeccionesSegunConsenso();
    }

    @Scheduled(cron = "0 0 * * * *")
    public void refrescarHechosYColecciones() {
        try {
            log.info("⌛ [Scheduler] Actualizando hechos…");
            agregadorService.actualizarHechos();
            log.info("✅ [Scheduler] Hechos actualizados.");
        } catch (Exception e) {
            log.error("❌ [Scheduler] Error al actualizar hechos", e);
        }
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void actualizarColeccionesSegunConsenso() {
        try {
            log.info("⌛ [Scheduler] Actualizando hechos consensuados de todas las colecciones…");
            long t0 = System.currentTimeMillis();
            coleccionesService.actualizarHechosSegunConsenso();
            log.info("✅ [Scheduler] Consenso actualizado en {} ms", System.currentTimeMillis() - t0);
        } catch (Exception e) {
            log.error("❌ [Scheduler] Error al actualizar consenso", e);
        }
    }
}