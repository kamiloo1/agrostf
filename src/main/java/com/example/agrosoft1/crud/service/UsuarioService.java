package com.example.agrosoft1.crud.service;

import com.example.agrosoft1.crud.entity.Role;
import com.example.agrosoft1.crud.entity.Usuario;
import com.example.agrosoft1.crud.repository.RoleRepository;
import com.example.agrosoft1.crud.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Método de login (compatibilidad)
    public Optional<Usuario> login(String email, String contrasena) {
        Usuario usuario = usuarioRepository.findByCorreoAndPassword(email, contrasena);
        return Optional.ofNullable(usuario);
    }

    // Buscar usuario por correo
    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByCorreo(email);
    }

    // Buscar usuario por correo (alias)
    public Optional<Usuario> findByCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    // Buscar usuario por número de documento
    public Optional<Usuario> findByNumeroDocumento(String numeroDocumento) {
        return usuarioRepository.findByNumeroDocumento(numeroDocumento);
    }

    // Listar todos los usuarios
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    // Guardar usuario (nuevo registro - sin rol asignado)
    public Usuario guardarUsuario(Usuario usuario) {
        // Validación: No duplicar correos
        if (usuario.getCorreo() != null) {
            Optional<Usuario> usuarioExistente = usuarioRepository.findByCorreo(usuario.getCorreo());
            if (usuarioExistente.isPresent() && 
                (usuario.getId() == null || !usuarioExistente.get().getId().equals(usuario.getId()))) {
                throw new IllegalArgumentException("El correo electrónico ya está registrado. No se permiten correos duplicados.");
            }
        }
        
        // Validación: No duplicar número de documento
        if (usuario.getNumeroDocumento() != null && !usuario.getNumeroDocumento().trim().isEmpty()) {
            Optional<Usuario> docExistente = usuarioRepository.findByNumeroDocumento(usuario.getNumeroDocumento());
            if (docExistente.isPresent() && 
                (usuario.getId() == null || !docExistente.get().getId().equals(usuario.getId()))) {
                throw new IllegalArgumentException("El número de documento ya está registrado. No se permiten documentos duplicados.");
            }
        }
        
        // Validar formato de correo
        if (usuario.getCorreo() != null && !usuario.getCorreo().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("El formato del correo electrónico no es válido");
        }
        
        // Validar campos obligatorios
        if (usuario.getCorreo() == null || usuario.getCorreo().trim().isEmpty()) {
            throw new IllegalArgumentException("El correo electrónico es obligatorio");
        }
        
        if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }
        
        // Encriptar contraseña
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        
        // Si no tiene fecha de creación, establecerla
        if (usuario.getFechaCreacion() == null) {
            usuario.setFechaCreacion(LocalDateTime.now());
        }
        
        // Establecer activo por defecto
        if (usuario.getActivo() == null) {
            usuario.setActivo(true);
        }
        
        return usuarioRepository.save(usuario);
    }

    // Actualizar usuario
    public Usuario actualizarUsuario(Usuario usuario) {
        // Buscar el usuario existente
        Usuario existente = usuarioRepository.findById(usuario.getId())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validación: No duplicar correos (excepto el mismo usuario)
        if (usuario.getCorreo() != null && !usuario.getCorreo().equals(existente.getCorreo())) {
            Optional<Usuario> usuarioExistente = usuarioRepository.findByCorreo(usuario.getCorreo());
            if (usuarioExistente.isPresent()) {
                throw new IllegalArgumentException("El correo electrónico ya está registrado. No se permiten correos duplicados.");
            }
        }
        
        // Validación: No duplicar número de documento (excepto el mismo usuario)
        if (usuario.getNumeroDocumento() != null && !usuario.getNumeroDocumento().trim().isEmpty() 
            && !usuario.getNumeroDocumento().equals(existente.getNumeroDocumento())) {
            Optional<Usuario> docExistente = usuarioRepository.findByNumeroDocumento(usuario.getNumeroDocumento());
            if (docExistente.isPresent()) {
                throw new IllegalArgumentException("El número de documento ya está registrado. No se permiten documentos duplicados.");
            }
        }
        
        // Validar formato de correo
        if (usuario.getCorreo() != null && !usuario.getCorreo().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("El formato del correo electrónico no es válido");
        }

        // Actualizar campos
        if (usuario.getNombre() != null) {
            existente.setNombre(usuario.getNombre());
        }
        if (usuario.getCorreo() != null) {
            existente.setCorreo(usuario.getCorreo());
        }
        if (usuario.getTelefono() != null) {
            existente.setTelefono(usuario.getTelefono());
        }
        if (usuario.getNumeroDocumento() != null) {
            existente.setNumeroDocumento(usuario.getNumeroDocumento());
        }
        if (usuario.getRole() != null) {
            existente.setRole(usuario.getRole());
        }
        if (usuario.getActivo() != null) {
            existente.setActivo(usuario.getActivo());
        }

        // Solo actualiza la contraseña si se envía una nueva
        if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
            existente.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }

        return usuarioRepository.save(existente);
    }

    // Eliminar usuario
    public void eliminarUsuario(Integer id) {
        usuarioRepository.deleteById(id);
    }

    // Obtener usuario por ID
    public Optional<Usuario> obtenerUsuarioPorId(Integer id) {
        return usuarioRepository.findById(id);
    }

    // Asignar rol a usuario
    public Usuario asignarRol(Integer usuarioId, Integer roleId) {
        Usuario usuario = obtenerUsuarioPorId(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        
        usuario.setRole(role);
        return usuarioRepository.save(usuario);
    }

    // Obtener rol por nombre
    public Optional<Role> obtenerRolPorNombre(String nombreRol) {
        return roleRepository.findByNombre(nombreRol);
    }
}