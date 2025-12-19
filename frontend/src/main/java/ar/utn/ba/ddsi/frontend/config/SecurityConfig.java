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
                        .ignoringRequestMatchers("/hechos/crear", "/hechos/**", "/solicitudes/**") // Sin CSRF para AJAX
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/registro", "/403").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/img/**", "/static/**").permitAll()
                        .requestMatchers("/", "/hechos", "/hechos/**").permitAll()
                        .requestMatchers("/colecciones", "/colecciones/**").permitAll()
                        .requestMatchers("/terminos", "/privacidad", "/contacto").permitAll()
                        .requestMatchers("/admin", "/admin/**").authenticated()
                        .requestMatchers("/mis-hechos", "/mis-hechos/**", "/misHechos", "/misHechos/**").authenticated()
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
