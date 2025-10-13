package ar.utn.ba.ddsi.estadistica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EstadisticaApplication {

  public static void main(String[] args) {
    SpringApplication.run(EstadisticaApplication.class, args);
  }

}
