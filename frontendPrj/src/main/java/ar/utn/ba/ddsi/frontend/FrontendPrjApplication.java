package ar.utn.ba.ddsi.frontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class FrontendPrjApplication {

	public static void main(String[] args) {
		SpringApplication.run(FrontendPrjApplication.class, args);
	}

}
