package com.example.agrosoft1.crud.service;

import com.example.agrosoft1.crud.entity.Role;
import com.example.agrosoft1.crud.entity.Usuario;
import com.example.agrosoft1.crud.pattern.observer.*;
import com.example.agrosoft1.crud.repository.RoleRepository;
import com.example.agrosoft1.crud.repository.UsuarioRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired(required = false)
    private AuditoriaService auditoriaService;
    
    // Patrón Observer: Sujeto observable para notificar cambios
    private final SujetoObservable sujetoObservable;
    
    @Autowired(required = false)
    private NotificacionEmailObserver emailObserver;
    
    @Autowired(required = false)
    private LoggingObserver loggingObserver;

    public UsuarioService(UsuarioRepository usuarioRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.sujetoObservable = new SujetoObservable();
    }
    
    /**
     * Inicializa los observadores después de la construcción
     * Se llama automáticamente después de que Spring inyecta las dependencias
     */
    @PostConstruct
    public void inicializarObservadores() {
        if (emailObserver != null) {
            sujetoObservable.agregarObservador(emailObserver);
        }
        if (loggingObserver != null) {
            sujetoObservable.agregarObservador(loggingObserver);
        }
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

    /** Conteo por nombre de rol en BD (ADMIN, VETERINARIO, TRABAJADOR). */
    public long contarUsuariosPorNombreRol(String nombreRol) {
        if (nombreRol == null || nombreRol.isBlank()) {
            return 0;
        }
        return usuarioRepository.countByRole_Nombre(nombreRol.trim());
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
        
        // Validación: No duplicar número de documento (solo si no está vacío)
        String numDoc = usuario.getNumeroDocumento() != null ? usuario.getNumeroDocumento().trim() : null;
        if (numDoc != null && !numDoc.isEmpty()) {
            Optional<Usuario> docExistente = usuarioRepository.findByNumeroDocumento(numDoc);
            if (docExistente.isPresent() && 
                (usuario.getId() == null || !docExistente.get().getId().equals(usuario.getId()))) {
                throw new IllegalArgumentException("El número de documento ya está registrado. No se permiten documentos duplicados.");
            }
            usuario.setNumeroDocumento(numDoc);
        } else {
            // Si viene vacío, establecer como null para evitar violación de UNIQUE
            usuario.setNumeroDocumento(null);
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
        
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        
        // Patrón Observer: Notificar a los observadores sobre la creación
        inicializarObservadores(); // Asegurar que los observadores estén registrados
        sujetoObservable.notificarObservadores("CREAR", usuarioGuardado, 
            "Usuario creado: " + usuarioGuardado.getCorreo());
        
        return usuarioGuardado;
    }

    // Actualizar usuario
    @SuppressWarnings("null")
    public Usuario actualizarUsuario(Usuario usuario) {
        // Buscar el usuario existente
        Integer id = usuario.getId();
        if (id == null) {
            throw new IllegalArgumentException("El ID del usuario es obligatorio para actualizar");
        }
        Usuario existente = usuarioRepository.findById(id)
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
        // Manejar numeroDocumento: si viene vacío, establecer como null para evitar violación de UNIQUE
        if (usuario.getNumeroDocumento() != null) {
            String numDoc = usuario.getNumeroDocumento().trim();
            existente.setNumeroDocumento(numDoc.isEmpty() ? null : numDoc);
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

        Usuario usuarioActualizado = usuarioRepository.save(existente);
        if (auditoriaService != null) {
            auditoriaService.registrar("ACTUALIZAR", "Usuario", usuarioActualizado.getId(), "Usuario actualizado: " + usuarioActualizado.getCorreo());
        }
        // Patrón Observer: Notificar a los observadores sobre la actualización
        inicializarObservadores(); // Asegurar que los observadores estén registrados
        sujetoObservable.notificarObservadores("ACTUALIZAR", usuarioActualizado, 
            "Usuario actualizado: " + usuarioActualizado.getCorreo());
        
        return usuarioActualizado;
    }

    // Eliminar usuario
    @SuppressWarnings("null")
    public void eliminarUsuario(Integer id) {
        // Obtener el usuario antes de eliminarlo para notificar
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (auditoriaService != null) {
                auditoriaService.registrar("ELIMINAR", "Usuario", usuario.getId(), "Usuario eliminado: " + usuario.getCorreo());
            }
            usuarioRepository.deleteById(id);
            
            // Patrón Observer: Notificar a los observadores sobre la eliminación
            inicializarObservadores(); // Asegurar que los observadores estén registrados
            sujetoObservable.notificarObservadores("ELIMINAR", usuario, 
                "Usuario eliminado: " + usuario.getCorreo());
        } else {
            usuarioRepository.deleteById(id);
        }
    }

    // Obtener usuario por ID
    @SuppressWarnings("null")
    public Optional<Usuario> obtenerUsuarioPorId(Integer id) {
        return usuarioRepository.findById(id);
    }

    // Asignar rol a usuario
    @SuppressWarnings("null")
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

    /**
     * Cambiar contraseña del usuario logueado. Verifica la contraseña actual y actualiza a la nueva.
     * @param correo Correo del usuario (usuario logueado)
     * @param contrasenaActual Contraseña actual en texto plano
     * @param contrasenaNueva Contraseña nueva en texto plano
     * @throws IllegalArgumentException si el usuario no existe, contraseña actual incorrecta o nueva vacía
     */
    public void cambiarContrasena(String correo, String contrasenaActual, String contrasenaNueva) {
        if (correo == null || correo.trim().isEmpty()) {
            throw new IllegalArgumentException("Correo es obligatorio");
        }
        Usuario usuario = usuarioRepository.findByCorreo(correo)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        if (contrasenaActual == null || contrasenaActual.isEmpty()) {
            throw new IllegalArgumentException("La contraseña actual es obligatoria");
        }
        if (!passwordEncoder.matches(contrasenaActual, usuario.getPassword())) {
            throw new IllegalArgumentException("La contraseña actual no es correcta");
        }
        if (contrasenaNueva == null || contrasenaNueva.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña nueva es obligatoria");
        }
        if (contrasenaNueva.length() < 6) {
            throw new IllegalArgumentException("La contraseña nueva debe tener al menos 6 caracteres");
        }
        usuario.setPassword(passwordEncoder.encode(contrasenaNueva.trim()));
        usuarioRepository.save(usuario);
    }

    // Cambiar estado de usuario (activar/desactivar)
    public void cambiarEstadoUsuario(Integer id, Boolean activo) {
        Usuario usuario = obtenerUsuarioPorId(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        usuario.setActivo(activo);
        usuarioRepository.save(usuario);
        if (auditoriaService != null) {
            auditoriaService.registrar(activo ? "ACTIVAR" : "DESACTIVAR", "Usuario", usuario.getId(), (activo ? "Usuario activado: " : "Usuario desactivado: ") + usuario.getCorreo());
        }
        // Patrón Observer: Notificar a los observadores sobre el cambio de estado
        inicializarObservadores();
        String accion = activo ? "ACTIVAR" : "DESACTIVAR";
        String mensaje = activo ? "Usuario activado: " : "Usuario desactivado: ";
        sujetoObservable.notificarObservadores(accion, usuario, mensaje + usuario.getCorreo());
    }
}