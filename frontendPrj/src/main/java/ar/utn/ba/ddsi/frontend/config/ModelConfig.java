package ar.utn.ba.ddsi.frontend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@Component
@ControllerAdvice
public class ModelConfig {

    @Value("${app.api.agregador.base-url:http://localhost:8084/api}")
    private String apiAgregadorBaseUrl;

    @ModelAttribute("apiBaseUrl")
    public String getApiBaseUrl() {
        return apiAgregadorBaseUrl;
    }
}

