package ar.utn.ba.ddsi.frontend.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Component
@ControllerAdvice
public class ModelConfig {

    @Value("${app.api.agregador.base-url:http://localhost:8084/api}")
    private String apiAgregadorBaseUrl;

    @ModelAttribute("apiBaseUrl")
    public String getApiBaseUrl() {
        return apiAgregadorBaseUrl;
    }

    // Public base URL (scheme + host) of the current request; honors X-Forwarded-* headers from nginx.
    @ModelAttribute("publicUrl")
    public String getPublicUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
    }

    @ModelAttribute("httpServletRequest")
    public HttpServletRequest getHttpServletRequest(HttpServletRequest request) {
        return request;
    }
}

