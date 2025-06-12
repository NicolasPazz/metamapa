package ar.utn.ba.ddsi.agregador.schedulers;

import ar.utn.ba.ddsi.agregador.service.impl.AgregadorService;
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

    @PostConstruct
    public void onStartup() {
        log.info("⌛ onStartup: Scheduler de agregador iniciado");

        agregadorService.actualizarHechos()
            .doOnTerminate(() -> log.info("✅ onStartup: Scheduler de agregador finalizado"))
            .subscribe();
    }

    @Scheduled(cron = "0 0 * * * *")
    public void refrescarHechosYColecciones() {
        agregadorService.actualizarHechos()
            .doOnError(e -> log.error("[Scheduler] Error al actualizar hechos", e))
            .subscribe();
    }
}