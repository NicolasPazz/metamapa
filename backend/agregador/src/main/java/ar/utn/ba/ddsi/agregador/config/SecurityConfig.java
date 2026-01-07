package ar.utn.ba.ddsi.agregador.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter, OriginFilter originFilter) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(originFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> {
                auth.requestMatchers("/actuator/**").permitAll();
                
                auth.requestMatchers("/api/hechos").permitAll();
                auth.requestMatchers("/api/colecciones/**").permitAll();
                auth.requestMatchers("/api/fuentes/**").permitAll();
                auth.requestMatchers("/api/front/**").permitAll();
                auth.requestMatchers("/api/multimedia/**").permitAll();
                auth.requestMatchers("/api/solicitudes").permitAll();
                
                auth.requestMatchers("/api/admin/**").authenticated();
                auth.requestMatchers("/api/hechos/*/ocultar").authenticated();
                
                auth.anyRequest().permitAll();
            });

        return http.build();
    }
}

