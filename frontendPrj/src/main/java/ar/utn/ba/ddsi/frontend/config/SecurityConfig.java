package ar.utn.ba.ddsi.frontend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas - acceso sin autenticación
                // La página de login y los recursos estáticos son públicos
                .requestMatchers("/login", "/registro").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/img/**", "/static/**").permitAll()
                // Rutas públicas del sistema
                .requestMatchers("/", "/hechos", "/hechos/**").permitAll()
                .requestMatchers("/colecciones", "/colecciones/**").permitAll()
                .requestMatchers("/terminos", "/privacidad", "/contacto").permitAll()
                // Rutas que requieren autenticación
                // Cualquier ruta que contenga "admin" requiere autenticación
                .requestMatchers("/admin", "/admin/**").authenticated()
                // Rutas personales como "mis hechos" requieren autenticación
                .requestMatchers("/mis-hechos", "/mis-hechos/**", "/misHechos", "/misHechos/**").authenticated()
                // Por defecto, todas las demás rutas son públicas
                .anyRequest().permitAll()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
                // después del login exitoso, redirigir
                // Por ahora redirigimos a home, pero esto se puede personalizar por rol
                .defaultSuccessUrl("/", true)
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .exceptionHandling(exceptions -> exceptions
                // si no está autenticado y accede a ruta protegida, redirigir a login
                .authenticationEntryPoint((request, response, authException) -> {
                    response.sendRedirect("/login");
                })
                // si está autenticado pero no tiene permisos, redirigir a 403
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.sendRedirect("/403");
                })
            );

        return http.build();
    }
}
