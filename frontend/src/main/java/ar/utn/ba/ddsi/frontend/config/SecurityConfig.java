package ar.utn.ba.ddsi.frontend.config;

import ar.utn.ba.ddsi.frontend.providers.CustomAuthProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;

@EnableMethodSecurity()
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
                        .ignoringRequestMatchers("/hechos/crear", "/hechos/**", "/solicitudes/**") // Sin CSRF para AJAX
                )
                .authorizeHttpRequests(auth -> auth
                        // Recursos estáticos públicos
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/img/**", "/static/**").permitAll()
                        // Health checks públicos
                        .requestMatchers("/health", "/actuator/health").permitAll()
                        // Páginas de acceso público
                        .requestMatchers("/login", "/registro", "/403").permitAll()
                        // Páginas legales públicas
                        .requestMatchers("/terminos", "/privacidad").permitAll()
                        // Landing y páginas públicas
                        .requestMatchers("/").permitAll()
                        // Colecciones públicas (debe ir antes de admin para evitar conflictos)
                        .requestMatchers("/colecciones", "/colecciones/**").permitAll()
                        // Endpoints de administración - requieren rol ADMIN (debe ir antes de otros para evitar conflictos)
                        .requestMatchers("/admin", "/admin/**").hasRole("ADMIN")
                        // Endpoints de hechos que requieren autenticación (más específicos primero)
                        .requestMatchers("/hechos/mis-hechos", "/hechos/mis-hechos/**", "/hechos/misHechos", "/hechos/misHechos/**").authenticated()
                        .requestMatchers("/hechos/nuevo", "/hechos/nuevo/**").authenticated()
                        .requestMatchers("/hechos/crear", "/hechos/crear/**").authenticated()
                        // Endpoints de hechos públicos (debe ir después de las reglas específicas)
                        .requestMatchers("/hechos", "/hechos/**").permitAll()
                        // Endpoints de solicitudes que requieren autenticación (más específicos primero)
                        .requestMatchers("/solicitudes/modificar/**").authenticated()
                        // PATCH /solicitudes/{id} requiere autenticación - se maneja por patrón
                        .requestMatchers(request -> {
                            String uri = request.getRequestURI();
                            String method = request.getMethod();
                            return uri.matches("/solicitudes/\\d+") && "PATCH".equals(method);
                        }).authenticated()
                        // GET /solicitudes (sin parámetros) requiere ADMIN (AdminController)
                        .requestMatchers(request -> {
                            String uri = request.getRequestURI();
                            String method = request.getMethod();
                            return "/solicitudes".equals(uri) && "GET".equals(method);
                        }).hasRole("ADMIN")
                        // Resto de solicitudes públicas (crear solicitud es público)
                        .requestMatchers("/solicitudes", "/solicitudes/**").permitAll()
                        // Cualquier otra petición es pública por defecto (para mantener compatibilidad)
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
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendRedirect("/login?unauthorized")
                        )
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.sendRedirect("/403")
                        )
                );

        return http.build();
    }
}
