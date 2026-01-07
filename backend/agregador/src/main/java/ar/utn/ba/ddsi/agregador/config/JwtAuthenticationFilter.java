package ar.utn.ba.ddsi.agregador.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Clave compartida con el servicio users - debe ser la misma en todos los servicios
    // Esta clave coincide con la de JwtUtil en el servicio users
    private static final SecretKey key = Keys.hmacShaKeyFor("metamapa-secret-key-for-jwt-token-generation-should-be-long-enough".getBytes());
    
    public JwtAuthenticationFilter() {
        log.info("[JwtAuthenticationFilter] Filtro JWT inicializado");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String path = request.getRequestURI();
        log.info("[JwtAuthenticationFilter] FILTRO EJECUTADO - Procesando petición: {} {}", request.getMethod(), path);
        
        // Solo validar JWT para endpoints admin
        if (path.startsWith("/api/admin") || path.matches("/api/hechos/\\d+/ocultar")) {
            log.info("[JwtAuthenticationFilter] Endpoint protegido detectado: {}", path);
            
            // Permitir llamadas internas entre servicios sin JWT
            if (isInternalServiceRequest(request)) {
                log.info("[JwtAuthenticationFilter] Llamada interna detectada, permitiendo acceso sin JWT para: {}", path);
                // Establecer autenticación básica para servicios internos
                var auth = new UsernamePasswordAuthenticationToken(
                    "internal-service",
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
                filterChain.doFilter(request, response);
                return;
            }
            
            String header = request.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                try {
                    Claims claims = Jwts.parserBuilder()
                            .setSigningKey(key)
                            .build()
                            .parseClaimsJws(token)
                            .getBody();
                    
                    String username = claims.getSubject();
                    var auth = new UsernamePasswordAuthenticationToken(
                        username, 
                        null, 
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    log.info("[JwtAuthenticationFilter] Usuario autenticado: {}", username);
                } catch (Exception e) {
                    log.warn("[JwtAuthenticationFilter] Token JWT inválido: {}", e.getMessage());
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido");
                    return;
                }
            } else {
                log.warn("[JwtAuthenticationFilter] Intento de acceso a endpoint protegido sin token: {}", path);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Se requiere autenticación");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
    
    /**
     * Obtiene la dirección IP remota considerando X-Forwarded-For header
     */
    private String getRemoteAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
    
    /**
     * Verifica si la petición proviene de un servicio interno (red Docker o localhost)
     * Permite llamadas service-to-service sin autenticación JWT
     */
    private boolean isInternalServiceRequest(HttpServletRequest request) {
        String address = getRemoteAddress(request);
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

