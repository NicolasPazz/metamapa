package ar.utn.ba.ddsi.frontend.config;

import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

/**
 * Agrega el header X-Internal-Token (variable INTERNAL_API_TOKEN) a las llamadas del
 * frontend a los servicios backend. Los backends lo usan para aceptar requests que no
 * llegan desde una red privada (ej. Render). Si la variable no esta definida, no hace nada.
 */
@Configuration
public class InternalTokenConfig {

    private static final String TOKEN = System.getenv().getOrDefault("INTERNAL_API_TOKEN", "");

    public static void agregarToken(HttpHeaders headers) {
        if (!TOKEN.isBlank()) {
            headers.set("X-Internal-Token", TOKEN);
        }
    }

    @Bean
    public WebClientCustomizer internalTokenCustomizer() {
        return builder -> builder.defaultHeaders(InternalTokenConfig::agregarToken);
    }
}
