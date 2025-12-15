package ar.utn.ba.ddsi.agregador;

import ar.utn.ba.ddsi.agregador.domain.fuente.config.FuentesProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(FuentesProperties.class)
public class AgregadorApplication {

  public static void main(String[] args) {
    SpringApplication.run(AgregadorApplication.class, args);
  }

}
