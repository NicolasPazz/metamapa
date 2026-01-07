package ar.utn.ba.ddsi.agregador.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;
import java.util.TimeZone;

@Configuration
public class TimeZoneConfig {

    @PostConstruct
    public void init() {
        // Establecer UTC como zona horaria por defecto para toda la aplicación
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("UTC")));
    }
}

