package ar.utn.ba.ddsi.estatica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class EstaticaApplication {

  public static void main(String[] args) {
    SpringApplication.run(EstaticaApplication.class, args);
  }

}
