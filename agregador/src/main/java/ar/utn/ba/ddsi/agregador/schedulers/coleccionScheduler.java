package ar.utn.ba.ddsi.agregador.schedulers;

import org.springframework.scheduling.annotation.Scheduled;

public class coleccionScheduler {
    //private AgregadorService agregadorService;

    /*public coleccionScheduler(AgregadorService agregadorService) {
        this.agregadorService = agregadorService;
    }*/

    @Scheduled(cron = "* * 1 * * *")
    public void refrescarColecciones() throws Exception {
        //this.agregadorService.actualizarColecciones();
    }


}