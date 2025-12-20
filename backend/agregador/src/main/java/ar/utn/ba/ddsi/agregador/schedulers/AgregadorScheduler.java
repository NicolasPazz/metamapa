package ar.utn.ba.ddsi.agregador.schedulers;

import ar.utn.ba.ddsi.agregador.service.AgregadorService;
import ar.utn.ba.ddsi.agregador.service.ColeccionesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AgregadorScheduler {

    private final AgregadorService agregadorService;
    private final ColeccionesService coleccionesService;


    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        try {
            agregadorService.actualizarHechos();
        } catch (Exception e) {
            log.error("[Scheduler] Error en startup actualizando hechos", e);
        }
        actualizarColeccionesSegunConsenso();
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