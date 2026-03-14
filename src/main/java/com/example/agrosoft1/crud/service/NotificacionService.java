package com.example.agrosoft1.crud.service;

import com.example.agrosoft1.crud.entity.Notificacion;
import com.example.agrosoft1.crud.entity.Usuario;
import com.example.agrosoft1.crud.repository.NotificacionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final UsuarioService usuarioService;

    public NotificacionService(NotificacionRepository notificacionRepository, UsuarioService usuarioService) {
        this.notificacionRepository = notificacionRepository;
        this.usuarioService = usuarioService;
    }

    /**
     * Crea una notificación para un usuario por correo.
     */
    @Transactional
    public Notificacion crear(String correoUsuario, String mensaje, String tipo) {
        return usuarioService.findByCorreo(correoUsuario)
                .map(u -> crear(u, mensaje, tipo, null))
                .orElse(null);
    }

    /**
     * Crea una notificación para un usuario por correo, con enlace.
     */
    @Transactional
    public Notificacion crear(String correoUsuario, String mensaje, String tipo, String enlace) {
        return usuarioService.findByCorreo(correoUsuario)
                .map(u -> crear(u, mensaje, tipo, enlace))
                .orElse(null);
    }

    /**
     * Crea una notificación para un usuario.
     */
    @Transactional
    public Notificacion crear(Usuario usuario, String mensaje, String tipo) {
        return crear(usuario, mensaje, tipo, null);
    }

    /**
     * Crea una notificación para un usuario, con enlace opcional.
     */
    @Transactional
    public Notificacion crear(Usuario usuario, String mensaje, String tipo, String enlace) {
        if (usuario == null || mensaje == null || mensaje.isBlank()) return null;
        Notificacion n = new Notificacion(usuario, mensaje, tipo != null ? tipo : "SISTEMA", enlace);
        return notificacionRepository.save(n);
    }

    /**
     * Notifica a todos los administradores.
     */
    @Transactional
    public void notificarAdministradores(String mensaje, String tipo, String enlace) {
        usuarioService.listarUsuarios().stream()
                .filter(u -> u.getRole() != null && "ADMIN".equalsIgnoreCase(u.getRole().getNombre()))
                .filter(Usuario::getActivo)
                .forEach(u -> crear(u, mensaje, tipo, enlace));
    }

    /**
     * Obtiene las últimas notificaciones del usuario (máx 10).
     */
    public List<Notificacion> listarRecientes(String correoUsuario, int limite) {
        return usuarioService.findByCorreo(correoUsuario)
                .map(u -> notificacionRepository.findByUsuarioOrderByFechaCreacionDesc(u, PageRequest.of(0, limite)))
                .orElse(List.of());
    }

    /**
     * Cuenta las notificaciones no leídas del usuario.
     */
    public long contarNoLeidas(String correoUsuario) {
        return usuarioService.findByCorreo(correoUsuario)
                .map(u -> notificacionRepository.countByUsuarioAndLeidaFalse(u))
                .orElse(0L);
    }

    /**
     * Marca una notificación como leída.
     */
    @Transactional
    public boolean marcarComoLeida(Long id, String correoUsuario) {
        return usuarioService.findByCorreo(correoUsuario)
                .map(u -> notificacionRepository.marcarComoLeida(id, u) > 0)
                .orElse(false);
    }

    /**
     * Marca todas las notificaciones del usuario como leídas.
     */
    @Transactional
    public void marcarTodasComoLeidas(String correoUsuario) {
        usuarioService.findByCorreo(correoUsuario)
                .ifPresent(notificacionRepository::marcarTodasComoLeidas);
    }
}
