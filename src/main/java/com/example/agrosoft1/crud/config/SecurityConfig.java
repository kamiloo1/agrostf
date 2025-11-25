package com.example.agrosoft1.crud.config;

import com.example.agrosoft1.crud.service.CustomUserDetailsService;
import com.example.agrosoft1.crud.service.UsuarioService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CustomUserDetailsService userDetailsService(UsuarioService usuarioService) {
        return new CustomUserDetailsService(usuarioService);
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            String role = authentication.getAuthorities().iterator().next().getAuthority();
            
            switch (role) {
                case "ROLE_ADMIN":
                    response.sendRedirect("/dashboard/administrador");
                    break;
                case "ROLE_VETERINARIO":
                    response.sendRedirect("/dashboard/veterinario");
                    break;
                case "ROLE_TRABAJADOR":
                    response.sendRedirect("/dashboard/trabajador");
                    break;
                default:
                    response.sendRedirect("/login?error=true");
                    break;
            }
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CustomUserDetailsService userDetailsService) throws Exception {
        http
            .userDetailsService(userDetailsService)
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/login", "/registrarse", "/registro/**", "/static/**", "/images/**").permitAll()
                .requestMatchers("/dashboard/administrador", "/admin/**").hasRole("ADMIN")
                .requestMatchers("/dashboard/veterinario", "/vet/**").hasRole("VETERINARIO")
                .requestMatchers("/dashboard/trabajador", "/trabajador/**").hasRole("TRABAJADOR")
                .requestMatchers("/clima/**").authenticated() // Clima accesible para todos los usuarios autenticados
                .requestMatchers("/api/**").permitAll() // APIs públicas si es necesario
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login/auth")
                .successHandler(customAuthenticationSuccessHandler())
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .csrf(csrf -> csrf.disable()) // Deshabilitamos CSRF para simplificar
            .sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            );

        return http.build();
    }
}
