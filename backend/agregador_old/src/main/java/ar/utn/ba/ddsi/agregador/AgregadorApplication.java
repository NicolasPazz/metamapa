package ar.utn.ba.ddsi.agregador;

import ar.utn.ba.ddsi.agregador.config.FuentesProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(FuentesProperties.class)
public class AgregadorApplication {

  public static void main(String[] args) {
    SpringApplication.run(AgregadorApplication.class, args);
  }

}
