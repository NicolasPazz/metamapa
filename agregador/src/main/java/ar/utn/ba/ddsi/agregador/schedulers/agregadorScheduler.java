package ar.utn.ba.ddsi.agregador.schedulers;

import ar.utn.ba.ddsi.agregador.service.impl.AgregadorService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class agregadorScheduler {

    private final AgregadorService agregadorService;

    @PostConstruct
    public void onStartup() throws Exception {
        System.out.println("⌛ onStartup: Scheduler de agregador iniciado");
        this.refrescarHechosYColecciones();
        System.out.println("✅ onStartup: Scheduler de agregador finalizado");
    }

    @Scheduled(cron = "0 0 * * * *")
    public void refrescarHechosYColecciones() throws Exception {
        this.agregadorService.actualizarHechos();
    }

    @Scheduled(cron = "0 0 * * * *")
    public void revisarSpamSolicitudes() {
        this.agregadorService.revisarSolicitudes();
    }
}