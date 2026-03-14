package com.example.agrosoft1.crud.service;

import com.example.agrosoft1.crud.entity.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    
    private final UsuarioService usuarioService;

    public CustomUserDetailsService(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        logger.info("Intentando autenticar usuario: {}", correo);
        
        Usuario usuario = usuarioService.findByCorreo(correo)
                .orElseThrow(() -> {
                    logger.error("Usuario no encontrado: {}", correo);
                    return new UsernameNotFoundException("Usuario no encontrado: " + correo);
                });

        logger.info("Usuario encontrado: {} - Rol: {} - Activo: {}", 
            usuario.getCorreo(), 
            usuario.getRole() != null ? usuario.getRole().getNombre() : "SIN ROL",
            usuario.getActivo());

        // Verificar que el usuario tenga rol asignado y esté activo
        if (usuario.getRole() == null) {
            logger.error("Usuario {} no tiene rol asignado", correo);
            throw new UsernameNotFoundException("Usuario sin rol asignado. Contacte al administrador.");
        }

        if (usuario.getActivo() == null || !usuario.getActivo()) {
            logger.error("Usuario {} está inactivo", correo);
            throw new UsernameNotFoundException("Usuario inactivo. Contacte al administrador.");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        String nombreRol = usuario.getRole().getNombre().toUpperCase().trim();
        
        // Normalizar el nombre del rol para asegurar consistencia
        // Si el rol es "ADMINISTRADOR", convertirlo a "ADMIN"
        String rolNormalizado = nombreRol;
        if (nombreRol.equals("ADMINISTRADOR") || nombreRol.startsWith("ADMIN")) {
            rolNormalizado = "ADMIN";
        } else if (nombreRol.contains("TRABAJADOR") || nombreRol.contains("AGRICOLA") || nombreRol.contains("AGRÍCOLA")) {
            rolNormalizado = "TRABAJADOR";
        } else if (nombreRol.contains("VETERINARIO") || nombreRol.contains("VET")) {
            rolNormalizado = "VETERINARIO";
        }
        
        String authority = "ROLE_" + rolNormalizado;
        authorities.add(new SimpleGrantedAuthority(authority));
        
        logger.info("=== AUTENTICACIÓN ===");
        logger.info("Usuario: {}", correo);
        logger.info("Rol en BD: {}", nombreRol);
        logger.info("Rol normalizado: {}", rolNormalizado);
        logger.info("Autoridad asignada: {}", authority);
        logger.info("====================");

        return new User(usuario.getCorreo(), usuario.getPassword(), authorities);
    }
}
