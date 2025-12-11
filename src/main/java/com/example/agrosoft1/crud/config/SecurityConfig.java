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
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

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
            try {
                if (authentication == null || authentication.getAuthorities() == null || 
                    !authentication.getAuthorities().iterator().hasNext()) {
                    logger.warn("Autenticación sin autoridades - redirigiendo a login");
                    response.sendRedirect("/login?error=sin_rol");
                    return;
                }
                
                String role = authentication.getAuthorities().iterator().next().getAuthority();
                logger.info("Usuario autenticado con rol: {} - URI solicitada: {}", role, request.getRequestURI());
                
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
                        logger.warn("Rol no reconocido: {} - redirigiendo a login", role);
                        response.sendRedirect("/login?error=rol_no_valido");
                        break;
                }
            } catch (Exception e) {
                logger.error("Error en AuthenticationSuccessHandler: {}", e.getMessage(), e);
                response.sendRedirect("/login?error=error_sistema");
            }
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            String usuario = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "NO AUTENTICADO";
            String uri = request.getRequestURI();
            String errorMsg = accessDeniedException.getMessage();
            
            // Obtener las autoridades del usuario autenticado
            String autoridades = "N/A";
            if (request.getUserPrincipal() instanceof org.springframework.security.core.Authentication) {
                org.springframework.security.core.Authentication auth = 
                    (org.springframework.security.core.Authentication) request.getUserPrincipal();
                autoridades = auth.getAuthorities().stream()
                    .map(a -> a.getAuthority())
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("SIN AUTORIDADES");
            }
            
            logger.error("=== ACCESO DENEGADO (403) ===");
            logger.error("Usuario: {}", usuario);
            logger.error("URI solicitada: {}", uri);
            logger.error("Autoridades del usuario: {}", autoridades);
            logger.error("Autoridad requerida: ROLE_ADMIN");
            logger.error("Error: {}", errorMsg);
            logger.error("============================");
            
            // Si el usuario no está autenticado, redirigir a login
            if (request.getUserPrincipal() == null) {
                response.sendRedirect("/login?error=no_autenticado");
            } else {
                // Si está autenticado pero no tiene permisos, mostrar error
                response.sendRedirect("/login?error=sin_permisos");
            }
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CustomUserDetailsService userDetailsService) throws Exception {
        http
            .userDetailsService(userDetailsService)
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/inicio", "/login", "/registrarse", "/registro/**", "/static/**", "/images/**", "/css/**", "/js/**").permitAll()
                .requestMatchers("/admin/reportes/**").authenticated() // Reportes accesibles para todos los usuarios autenticados (debe ir antes de /admin/**)
                .requestMatchers("/dashboard/administrador", "/admin/**").hasAnyAuthority("ROLE_ADMIN")
                .requestMatchers("/dashboard/veterinario", "/vet/**").hasAnyAuthority("ROLE_VETERINARIO")
                .requestMatchers("/dashboard/trabajador", "/trabajador/**").hasAnyAuthority("ROLE_TRABAJADOR")
                  .requestMatchers("/clima/**").authenticated() // Clima accesible para todos los usuarios autenticados
                  .requestMatchers("/api/**").permitAll() // APIs públicas si es necesario
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login/auth")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(customAuthenticationSuccessHandler())
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .csrf(csrf -> csrf.disable()) // Deshabilitamos CSRF para simplificar
            .exceptionHandling(exceptions -> exceptions
                .accessDeniedHandler(accessDeniedHandler())
            )
            .sessionManagement(session -> {
                session.maximumSessions(1)
                       .maxSessionsPreventsLogin(false);
                session.sessionFixation().migrateSession();
            })
            .headers(headers -> headers
                .frameOptions(frame -> frame.deny())
                .contentTypeOptions(contentType -> {})
            );

        return http.build();
    }
}
