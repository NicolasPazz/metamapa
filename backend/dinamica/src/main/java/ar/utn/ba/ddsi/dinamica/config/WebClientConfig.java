package ar.utn.ba.ddsi.dinamica.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    
    @Bean
    public WebClient webClientAgregador(@Value("${agregador.url:http://localhost:8084}/api") String agregadorUrl) {
        String internalToken = System.getenv().getOrDefault("INTERNAL_API_TOKEN", "");
        return WebClient.builder()
                .baseUrl(agregadorUrl)
                .defaultHeaders(h -> { if (!internalToken.isBlank()) h.set("X-Internal-Token", internalToken); })
                .build();
    }
}

