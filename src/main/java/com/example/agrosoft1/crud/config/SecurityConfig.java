package com.example.agrosoft1.crud.config;

import com.example.agrosoft1.crud.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
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

    // No definir otro bean UserDetailsService: se usa el único CustomUserDetailsService (@Service)
    // para evitar "Found 2 UserDetailsService beans" que bloqueaba el login.

    /** Redirige al dashboard según rol. sendRedirect + flushBuffer para que el navegador reciba el 302 de inmediato. */
    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            if (authentication == null || authentication.getAuthorities() == null ||
                !authentication.getAuthorities().iterator().hasNext()) {
                logger.warn("Autenticación sin autoridades");
                response.sendRedirect(request.getContextPath() + "/login?error=sin_rol");
                return;
            }
            String role = authentication.getAuthorities().iterator().next().getAuthority();
            logger.info("Login OK, rol: {} -> redirigiendo", role);
            String path = "/dashboard/administrador";
            if ("ROLE_VETERINARIO".equals(role)) path = "/dashboard/veterinario";
            else if ("ROLE_TRABAJADOR".equals(role)) path = "/dashboard/trabajador";
            else if (!"ROLE_ADMIN".equals(role)) path = "/login?error=rol_no_valido";
            String targetUrl = request.getContextPath() + path;
            response.sendRedirect(targetUrl);
        };
    }

    /** Registra en log el motivo del fallo de login y redirige a /login con error concreto (inactivo, true, etc.). */
    @Bean
    public AuthenticationFailureHandler customAuthenticationFailureHandler() {
        return (request, response, exception) -> {
            String correo = request.getParameter("username");
            logger.error("=== FALLO DE LOGIN ===");
            logger.error("Correo intentado: {}", correo != null ? correo : "(vacío)");
            logger.error("Tipo: {} - Mensaje: {}", exception.getClass().getSimpleName(), exception.getMessage());
            if (exception.getCause() != null) {
                logger.error("Causa: {}", exception.getCause().getMessage());
            }
            logger.error("============================");
            String msg = exception.getMessage() != null ? exception.getMessage() : "";
            String errorParam = (msg.contains("inactivo") || msg.contains("inactive")) ? "inactivo" : "true";
            String ctx = request.getContextPath();
            response.sendRedirect(ctx + "/login?error=" + errorParam);
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
            
            String ctx = request.getContextPath();
            if (request.getUserPrincipal() == null) {
                response.sendRedirect(ctx + "/login?error=no_autenticado");
            } else {
                response.sendRedirect(ctx + "/login?error=sin_permisos");
            }
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CustomUserDetailsService userDetailsService) throws Exception {
        http
            .userDetailsService(userDetailsService)
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/inicio", "/login", "/registrarse", "/registro/**", "/recuperar", "/recuperar/**", "/error", "/static/**", "/images/**", "/css/**", "/js/**").permitAll()
                .requestMatchers("/admin/data/**", "/admin/data/cargar-ganado", "/admin/data/verificar").permitAll() // Endpoints para cargar datos (sin autenticación) - DEBE IR ANTES DE /admin/**
                .requestMatchers("/cuenta/**").authenticated()
                .requestMatchers("/admin/reportes/**", "/admin/busquedas", "/admin/busquedas/**").authenticated() // Reportes y Búsquedas: todos los autenticados
                .requestMatchers("/admin/ganado", "/admin/ganado/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_VETERINARIO", "ROLE_TRABAJADOR") // Ganado: todos los roles
                .requestMatchers("/admin/cultivos", "/admin/cultivos/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_TRABAJADOR")
                .requestMatchers("/dashboard/administrador", "/admin/**").hasAnyAuthority("ROLE_ADMIN")
                .requestMatchers("/vet/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_VETERINARIO") // Tratamientos: admin ve/verifica, vet gestiona
                .requestMatchers("/dashboard/veterinario").hasAnyAuthority("ROLE_ADMIN", "ROLE_VETERINARIO")
                .requestMatchers("/dashboard/trabajador").hasAnyAuthority("ROLE_TRABAJADOR")
                .requestMatchers("/trabajador/**").hasAnyAuthority("ROLE_VETERINARIO", "ROLE_TRABAJADOR") // Actividades: vet asigna/marca, trabajador ejecuta
                .requestMatchers("/clima", "/clima/**").authenticated()
                .requestMatchers("/api/**").permitAll() // APIs: session-config, notificaciones (el controlador valida auth)
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login/auth")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(customAuthenticationSuccessHandler())
                .failureHandler(customAuthenticationFailureHandler())
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
                .authenticationEntryPoint((request, response, authException) -> {
                    response.sendRedirect(request.getContextPath() + "/login");
                })
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
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives("connect-src 'self' https://cdn.jsdelivr.net https://api.openweathermap.org; default-src 'self'; script-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net; style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://fonts.googleapis.com; img-src 'self' data: https:; font-src 'self' https://cdn.jsdelivr.net https://fonts.gstatic.com")
                )
            );

        return http.build();
    }
}
