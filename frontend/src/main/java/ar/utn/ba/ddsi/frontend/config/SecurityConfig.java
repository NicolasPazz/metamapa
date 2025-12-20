package ar.utn.ba.ddsi.frontend.config;

import ar.utn.ba.ddsi.frontend.providers.CustomAuthProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@EnableMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig {

    @Bean
    public AuthenticationManager authManager(HttpSecurity http, CustomAuthProvider provider) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(provider)
                .build();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/hechos/crear", "/hechos/**", "/solicitudes/**", "/admin/agregar-etiquetas") // Permitir sin CSRF para AJAX
                )
                .authorizeHttpRequests(auth -> auth
                        // Recursos estáticos y login público
                        .requestMatchers("/login", "/registro", "/403").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/img/**", "/static/**").permitAll()
                        // Rutas públicas del sistema
                        .requestMatchers("/", "/hechos", "/hechos/**").permitAll()
                        .requestMatchers("/colecciones", "/colecciones/**").permitAll()
                        .requestMatchers("/terminos", "/privacidad", "/contacto").permitAll()
                        // Rutas que requieren autenticación
                        .requestMatchers("/admin", "/admin/**").authenticated()
                        .requestMatchers("/mis-hechos", "/mis-hechos/**", "/misHechos", "/misHechos/**").authenticated()
                        // Por defecto, todas las demás rutas son públicas
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                        .successHandler((request, response, authentication) -> {
                            boolean isAdmin = authentication.getAuthorities().stream()
                                    .map(GrantedAuthority::getAuthority)
                                    .anyMatch(authority -> authority.equals("ROLE_ADMIN"));
                            response.sendRedirect(isAdmin ? "/admin" : "/");
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout") // redirigir tras logout
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        // Usuario no autenticado → redirigir a login
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendRedirect("/login?unauthorized")
                        )
                        // Usuario autenticado pero sin permisos → redirigir a página de error
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.sendRedirect("/403")
                        )
                );

        return http.build();
    }
}
