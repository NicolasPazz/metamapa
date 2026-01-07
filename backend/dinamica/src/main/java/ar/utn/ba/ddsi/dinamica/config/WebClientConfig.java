package ar.utn.ba.ddsi.dinamica.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    
    @Bean
    public WebClient webClientAgregador(@Value("${agregador.url:http://localhost:8084}/api") String agregadorUrl) {
        return WebClient.builder()
                .baseUrl(agregadorUrl)
                .build();
    }
}

