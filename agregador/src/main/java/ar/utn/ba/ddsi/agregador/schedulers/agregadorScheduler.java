package ar.utn.ba.ddsi.agregador.schedulers;

import ar.utn.ba.ddsi.agregador.service.impl.AgregadorService;
import org.springframework.scheduling.annotation.Scheduled;

public class agregadorScheduler {
    private AgregadorService agregadorService;

    public agregadorScheduler(AgregadorService agregadorService) {
        this.agregadorService = agregadorService;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void refrescarHechosYColecciones() throws Exception {
        this.agregadorService.actualizarHechos();
        this.agregadorService.actualizarColecciones();

    }


}