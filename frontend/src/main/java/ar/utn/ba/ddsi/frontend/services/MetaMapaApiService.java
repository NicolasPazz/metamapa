package ar.utn.ba.ddsi.frontend.services;

import ar.utn.ba.ddsi.frontend.dto.AuthResponseDTO;
import ar.utn.ba.ddsi.frontend.dto.RolesPermisosDTO;
import ar.utn.ba.ddsi.frontend.services.internal.WebApiCallerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

@Service
public class MetaMapaApiService {

    private static final Logger log = LoggerFactory.getLogger(MetaMapaApiService.class);
    private final WebClient webClient;
    private final WebApiCallerService webApiCallerService;
    private final String authServiceUrl;

    @Autowired
    public MetaMapaApiService(
            WebApiCallerService webApiCallerService,
            @Value("${app.api.users.base-url:http://localhost:8087/api}") String authServiceUrl) {
        this.webClient = WebClient.builder().build();
        this.webApiCallerService = webApiCallerService;
        this.authServiceUrl = authServiceUrl;
    }

    public AuthResponseDTO login(String username, String password) {
        try {
            return webClient
                    .post()
                    .uri(authServiceUrl + "/auth")
                    .bodyValue(Map.of(
                            "username", username,
                            "password", password
                    ))
                    .retrieve()
                    .bodyToMono(AuthResponseDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error(e.getMessage());
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return null;
            }
            throw new RuntimeException("Error en el servicio de autenticación: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error de conexión con el servicio de autenticación: " + e.getMessage(), e);
        }
    }

    public void register(String username, String password, String nombre, String apellido) {
        try {
            webClient
                    .post()
                    .uri(authServiceUrl + "/auth/register")
                    .bodyValue(Map.of(
                            "username", username,
                            "password", password,
                            "nombre", nombre,
                            "apellido", apellido != null ? apellido : ""
                    ))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Error al registrar usuario: {}", e.getMessage());
            String errorMessage = "Error al registrar usuario";
            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                errorMessage = "Ese nombre de usuario ya está en uso";
            } else {
                try {
                    String responseBody = e.getResponseBodyAsString();
                    if (!responseBody.isEmpty()) {
                        if (responseBody.contains("\"error\"")) {
                            int errorStart = responseBody.indexOf("\"error\"");
                            int valueStart = responseBody.indexOf(":", errorStart) + 1;
                            int quoteStart = responseBody.indexOf("\"", valueStart) + 1;
                            int quoteEnd = responseBody.indexOf("\"", quoteStart);
                            if (quoteEnd > quoteStart) {
                                errorMessage = responseBody.substring(quoteStart, quoteEnd);
                            }
                        } else {
                            errorMessage = responseBody;
                        }
                    }
                } catch (Exception ex) {
                    log.warn("No se pudo parsear el mensaje de error: {}", ex.getMessage());
                }
            }
            throw new RuntimeException(errorMessage);
        } catch (Exception e) {
            log.error("Error de conexión al registrar usuario", e);
            throw new RuntimeException("Error de conexión con el servicio de autenticación: " + e.getMessage(), e);
        }
    }

    public RolesPermisosDTO getRolesPermisos(String accessToken) {
        try {
            return webApiCallerService.getWithAuth(
                authServiceUrl + "/auth/user/roles-permisos",
                accessToken,
                RolesPermisosDTO.class
            );
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error al obtener roles y permisos: " + e.getMessage(), e);
        }
    }
}
