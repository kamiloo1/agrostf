package com.example.agrosoft1.crud.gof;

import com.example.agrosoft1.crud.entity.Role;
import com.example.agrosoft1.crud.entity.Usuario;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Patrón Factory (GoF) para la creación de usuarios según su rol.
 * 
 * Este patrón encapsula la lógica de creación de objetos Usuario
 * según diferentes tipos (roles), simplificando el código cliente.
 * 
 * Ubicación: src/main/java/com/example/agrosoft1/crud/gof/
 * 
 * Uso en el sistema:
 * - DataInitializer: Para crear usuarios iniciales al iniciar el sistema
 * - Permite crear usuarios con configuraciones específicas según su rol
 * 
 * Beneficios del patrón:
 * - Encapsula la lógica de creación compleja
 * - Facilita la extensión para nuevos tipos de usuarios
 * - Centraliza la configuración por rol
 */
@Component
public class UsuarioFactory {
    
    private final PasswordEncoder passwordEncoder;
    
    public UsuarioFactory(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Método factory principal que crea un usuario según su rol
     * @param nombre Nombre del usuario
     * @param correo Correo electrónico
     * @param password Contraseña en texto plano (se encriptará)
     * @param telefono Teléfono (opcional)
     * @param numeroDocumento Número de documento (opcional)
     * @param role Rol del usuario
     * @return Usuario creado y configurado según su rol
     */
    public Usuario crearUsuario(String nombre, String correo, String password, 
                                String telefono, String numeroDocumento, Role role) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setTelefono(telefono);
        usuario.setNumeroDocumento(numeroDocumento);
        usuario.setRole(role);
        usuario.setActivo(true);
        usuario.setFechaCreacion(LocalDateTime.now());
        
        // Configuraciones específicas según el rol
        if (role != null) {
            String nombreRol = role.getNombre();
            switch (nombreRol.toUpperCase()) {
                case "ADMIN":
                    return configurarAdministrador(usuario);
                case "VETERINARIO":
                    return configurarVeterinario(usuario);
                case "TRABAJADOR":
                    return configurarTrabajador(usuario);
                default:
                    return usuario;
            }
        }
        
        return usuario;
    }
    
    /**
     * Configuración específica para usuarios Administradores
     */
    private Usuario configurarAdministrador(Usuario usuario) {
        // Los administradores siempre están activos
        usuario.setActivo(true);
        return usuario;
    }
    
    /**
     * Configuración específica para usuarios Veterinarios
     */
    private Usuario configurarVeterinario(Usuario usuario) {
        // Los veterinarios siempre están activos por defecto
        usuario.setActivo(true);
        return usuario;
    }
    
    /**
     * Configuración específica para usuarios Trabajadores
     */
    private Usuario configurarTrabajador(Usuario usuario) {
        // Los trabajadores están activos por defecto
        usuario.setActivo(true);
        return usuario;
    }
    
    /**
     * Método factory simplificado para crear administrador
     */
    public Usuario crearAdministrador(String nombre, String correo, String password, 
                                     String telefono, String numeroDocumento, Role role) {
        return crearUsuario(nombre, correo, password, telefono, numeroDocumento, role);
    }
    
    /**
     * Método factory simplificado para crear veterinario
     */
    public Usuario crearVeterinario(String nombre, String correo, String password, 
                                   String telefono, String numeroDocumento, Role role) {
        return crearUsuario(nombre, correo, password, telefono, numeroDocumento, role);
    }
    
    /**
     * Método factory simplificado para crear trabajador
     */
    public Usuario crearTrabajador(String nombre, String correo, String password, 
                                  String telefono, String numeroDocumento, Role role) {
        return crearUsuario(nombre, correo, password, telefono, numeroDocumento, role);
    }
}

