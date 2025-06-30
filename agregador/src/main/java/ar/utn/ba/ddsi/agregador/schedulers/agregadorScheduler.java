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

    @Scheduled(cron = "* * 3 * * *")
    public void actualizarColeccionesSegunConsenso() {
        new Thread(
                () -> {
                   try{ coleccionesService.actualizarHechosSegunConsenso();}
                   catch (Exception e) {
                       log.error("Error al actualizar colecciones");
                   }
                }
        ).start();
    }
}