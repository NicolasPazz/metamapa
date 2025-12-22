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

    // Clave compartida con el servicio users (debería estar en configuración)
    // Por ahora usamos una clave temporal - en producción debe ser la misma que users
    // NOTA: Esta clave debe coincidir con la del servicio users
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
}

