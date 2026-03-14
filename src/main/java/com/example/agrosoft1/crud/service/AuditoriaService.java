package com.example.agrosoft1.crud.service;

import com.example.agrosoft1.crud.entity.Auditoria;
import com.example.agrosoft1.crud.repository.AuditoriaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Servicio para registrar acciones de auditoría (crear, actualizar, eliminar) en entidades críticas.
 */
@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaService(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    /**
     * Registra una acción en la tabla de auditoría. El usuario se toma del contexto de seguridad si está autenticado.
     */
    public void registrar(String accion, String entidad, Object idEntidad, String detalles) {
        try {
            String usuario = null;
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && auth.getName() != null) {
                usuario = auth.getName();
            }
            Auditoria a = new Auditoria();
            a.setUsuario(usuario);
            a.setAccion(accion);
            a.setEntidad(entidad);
            a.setIdEntidad(idEntidad != null ? idEntidad.toString() : null);
            a.setFecha(LocalDateTime.now());
            a.setDetalles(detalles != null && detalles.length() > 500 ? detalles.substring(0, 500) : detalles);
            auditoriaRepository.save(a);
        } catch (Exception e) {
            // No fallar la operación principal si falla la auditoría
            org.slf4j.LoggerFactory.getLogger(AuditoriaService.class).warn("Error registrando auditoría: {}", e.getMessage());
        }
    }
}
