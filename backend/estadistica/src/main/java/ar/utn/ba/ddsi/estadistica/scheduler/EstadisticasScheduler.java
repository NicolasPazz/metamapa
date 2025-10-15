package ar.utn.ba.ddsi.estadistica.scheduler;

import ar.utn.ba.ddsi.estadistica.service.IEstadisticasService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class EstadisticasScheduler {

    private final IEstadisticasService estadisticas;

    @Scheduled(cron = "0 0 4 * * *")
    public void recalcularPeriodicamente() {
            log.info("Recalculo de estadísticas programado (4am) - {}", LocalDateTime.now());
            estadisticas.refrescoEstadisticas();
    }
}
