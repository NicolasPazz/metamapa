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
                        .ignoringRequestMatchers("/hechos/crear", "/hechos/**", "/solicitudes/**", "/api/**")
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/img/**", "/static/**").permitAll()
                        .requestMatchers("/health", "/actuator/health").permitAll()
                        .requestMatchers("/login", "/registro", "/403").permitAll()
                        .requestMatchers("/terminos", "/privacidad").permitAll()
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/colecciones", "/colecciones/**").permitAll()
                        .requestMatchers("/admin", "/admin/**").hasRole("ADMIN")
                        .requestMatchers("/hechos/mis-hechos", "/hechos/mis-hechos/**", "/hechos/misHechos", "/hechos/misHechos/**").authenticated()
                        .requestMatchers("/hechos", "/hechos/**").permitAll()
                        .requestMatchers("/solicitudes/modificar/**").authenticated()
                        .requestMatchers(request -> {
                            String uri = request.getRequestURI();
                            String method = request.getMethod();
                            return uri.matches("/solicitudes/\\d+") && "PATCH".equals(method);
                        }).authenticated()
                        .requestMatchers(request -> {
                            String uri = request.getRequestURI();
                            String method = request.getMethod();
                            return "/solicitudes".equals(uri) && "GET".equals(method);
                        }).hasRole("ADMIN")
                        .requestMatchers("/solicitudes", "/solicitudes/**").permitAll()
                        .requestMatchers("/api/solicitudes").permitAll()
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
                        .logoutSuccessUrl("/login?logout")
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
