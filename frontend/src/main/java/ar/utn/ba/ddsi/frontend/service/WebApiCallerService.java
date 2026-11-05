package ar.utn.ba.ddsi.frontend.service;

import ar.utn.ba.ddsi.frontend.dto.AuthResponseDTO;
import ar.utn.ba.ddsi.frontend.dto.RefreshTokenDTO;
import ar.utn.ba.ddsi.frontend.exceptions.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.util.List;

@Service
public class WebApiCallerService {

    private final WebClient webClient;
    private final String authServiceUrl;

    public WebApiCallerService(@Value("${app.api.users.base-url:http://localhost:8087/api}") String authServiceUrl) {
        this.webClient = WebClient.builder().defaultHeaders(ar.utn.ba.ddsi.frontend.config.InternalTokenConfig::agregarToken).build();
        this.authServiceUrl = authServiceUrl;
    }

    public <T> T executeWithTokenRetry(ApiCall<T> apiCall) {
        String accessToken = getAccessTokenFromSession();
        String refreshToken = getRefreshTokenFromSession();

        if (accessToken == null) {
            throw new RuntimeException("No hay token de acceso disponible");
        }

        try {
            return apiCall.execute(accessToken);
        } catch (WebClientResponseException e) {
            if ((e.getStatusCode() == HttpStatus.UNAUTHORIZED || e.getStatusCode() == HttpStatus.FORBIDDEN) && refreshToken != null) {
                try {
                    AuthResponseDTO newTokens = refreshToken(refreshToken);
                    
                    if (newTokens.getAccessToken() == null) {
                        throw new RuntimeException("Error al refrescar token: respuesta inválida");
                    }
                    
                    return apiCall.execute(newTokens.getAccessToken());
                } catch (Exception refreshError) {
                    throw new RuntimeException("Error al refrescar token y reintentar: " + refreshError.getMessage(), refreshError);
                }
            }
            if(e.getStatusCode() == HttpStatus.NOT_FOUND){
                throw new NotFoundException(e.getMessage());
            }
            throw new RuntimeException("Error en llamada al API: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error de conexión con el servicio: " + e.getMessage(), e);
        }
    }

    public <T> T get(String url, Class<T> responseType) {
        return executeWithTokenRetry(accessToken -> 
            webClient
                .get()
                .uri(url)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(responseType)
                .block()
        );
    }

    public <T> List<T> getList(String url, Class<T> responseType) {
        return executeWithTokenRetry(accessToken -> 
            webClient
                .get()
                .uri(url)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToFlux(responseType)
                .collectList()
                .block()
        );
    }

    public <T> T getWithAuth(String url, String accessToken, Class<T> responseType) {
        try {
            return webClient
                .get()
                .uri(url)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(responseType)
                .block();
        } catch (Exception e) {
            throw new RuntimeException("Error en llamada al API: " + e.getMessage(), e);
        }
    }

    public <T> T post(String url, Object body, Class<T> responseType) {
        return executeWithTokenRetry(accessToken -> 
            webClient
                .post()
                .uri(url)
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(responseType)
                .block()
        );
    }

    public <T> T put(String url, Object body, Class<T> responseType) {
        return executeWithTokenRetry(accessToken -> 
            webClient
                .put()
                .uri(url)
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(responseType)
                .block()
        );
    }

    public void delete(String url) {
        executeWithTokenRetry(accessToken -> {
            webClient
                .delete()
                .uri(url)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
            return null;
        });
    }

    private AuthResponseDTO refreshToken(String refreshToken) {
        try {
            RefreshTokenDTO refreshRequest = RefreshTokenDTO.builder()
                    .refreshToken(refreshToken)
                    .build();

            AuthResponseDTO response = webClient
                    .post()
                    .uri(authServiceUrl + "/auth/refresh")
                    .bodyValue(refreshRequest)
                    .retrieve()
                    .bodyToMono(AuthResponseDTO.class)
                    .block();

            if (response == null) {
                throw new RuntimeException("Error al refrescar token: respuesta nula");
            }
            updateTokensInSession(response.getAccessToken(), response.getRefreshToken());
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error al refrescar token: " + e.getMessage(), e);
        }
    }

    private String getAccessTokenFromSession() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        return (String) request.getSession().getAttribute("accessToken");
    }

    private String getRefreshTokenFromSession() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        return (String) request.getSession().getAttribute("refreshToken");
    }

    private void updateTokensInSession(String accessToken, String refreshToken) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        
        request.getSession().setAttribute("accessToken", accessToken);
        request.getSession().setAttribute("refreshToken", refreshToken);
    }

    @FunctionalInterface
    public interface ApiCall<T> {
        T execute(String accessToken) throws Exception;
    }
}
