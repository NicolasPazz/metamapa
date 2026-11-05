package ar.utn.ba.ddsi.estatica.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class OriginFilter extends OncePerRequestFilter {

    @Value("${security.origin-filter.enabled:true}")
    private boolean enabled;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String path = request.getRequestURI();
        
        // Permitir actuator sin restricción
        if (path.startsWith("/actuator/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Si el filtro está desactivado (desarrollo local), permitir todas las peticiones
        if (!enabled) {
            filterChain.doFilter(request, response);
            return;
        }

        // Servicios de MetaMapa que se llaman por URL publica (ej. Render) se identifican con un token compartido
        if (tieneTokenInterno(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String remoteAddr = getRemoteAddress(request);
        boolean isAllowed = isAllowedAddress(remoteAddr);
        
        if (!isAllowed) {
            log.warn("[OriginFilter] Request bloqueado desde origen no permitido: {} {} - RemoteAddr: {}", 
                    request.getMethod(), path, remoteAddr);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado: origen no permitido");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean tieneTokenInterno(HttpServletRequest request) {
        String token = System.getenv("INTERNAL_API_TOKEN");
        String recibido = request.getHeader("X-Internal-Token");
        return token != null && !token.isBlank() && recibido != null
            && java.security.MessageDigest.isEqual(token.getBytes(), recibido.getBytes());
    }

    private String getRemoteAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private boolean isAllowedAddress(String address) {
        if (address == null || address.isEmpty()) {
            return false;
        }
        
        // Localhost
        if (address.equals("127.0.0.1") || address.equals("::1") || address.equals("0:0:0:0:0:0:0:1")) {
            return true;
        }
        
        // Red Docker: 172.16.0.0 - 172.31.255.255
        if (address.startsWith("172.")) {
            String[] parts = address.split("\\.");
            if (parts.length >= 2) {
                try {
                    int secondOctet = Integer.parseInt(parts[1]);
                    if (secondOctet >= 16 && secondOctet <= 31) {
                        return true;
                    }
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        }
        
        // Red privada: 10.0.0.0 - 10.255.255.255
        return address.startsWith("10.");
    }
}

