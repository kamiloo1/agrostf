package com.example.agrosoft1.crud.pattern.observer;

import com.example.agrosoft1.crud.entity.Usuario;
import com.example.agrosoft1.crud.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Observador concreto que envía notificaciones por email
 * 
 * Implementa la interfaz Observador para enviar emails cuando ocurren eventos
 * relacionados con usuarios (crear, actualizar, eliminar)
 */
@Component
public class NotificacionEmailObserver implements Observador {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificacionEmailObserver.class);
    
    @Autowired(required = false)
    private EmailService emailService;
    
    @Override
    public void actualizar(String evento, Usuario usuario, Object datosAdicionales) {
        if (usuario == null) {
            logger.warn("Intento de notificar sobre usuario nulo");
            return;
        }
        
        try {
            String asunto = generarAsunto(evento, usuario);
            String cuerpo = generarCuerpoEmail(evento, usuario, datosAdicionales);
            
            // Solo enviar email si el servicio está disponible
            if (emailService != null && usuario.getCorreo() != null && !usuario.getCorreo().isEmpty()) {
                emailService.enviarCorreo(usuario.getCorreo(), asunto, cuerpo);
                logger.info("Email de notificación enviado a {} para evento: {}", 
                    usuario.getCorreo(), evento);
            } else {
                logger.debug("Email no enviado - servicio no disponible o correo vacío");
            }
        } catch (Exception e) {
            logger.error("Error al enviar email de notificación: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Genera el asunto del email según el evento
     */
    private String generarAsunto(String evento, Usuario usuario) {
        switch (evento.toUpperCase()) {
            case "CREAR":
                return "Bienvenido a AgroSoft - Cuenta Creada";
            case "ACTUALIZAR":
                return "Actualización de Cuenta - AgroSoft";
            case "ELIMINAR":
                return "Cuenta Eliminada - AgroSoft";
            default:
                return "Notificación de AgroSoft";
        }
    }
    
    /**
     * Genera el cuerpo del email según el evento
     */
    private String generarCuerpoEmail(String evento, Usuario usuario, Object datosAdicionales) {
        StringBuilder cuerpo = new StringBuilder();
        cuerpo.append("Estimado/a ").append(usuario.getNombre() != null ? usuario.getNombre() : "Usuario").append(",\n\n");
        
        switch (evento.toUpperCase()) {
            case "CREAR":
                cuerpo.append("Su cuenta ha sido creada exitosamente en AgroSoft.\n");
                cuerpo.append("Correo: ").append(usuario.getCorreo()).append("\n");
                cuerpo.append("Rol: ").append(usuario.getRole() != null ? usuario.getRole().getNombre() : "No asignado").append("\n");
                cuerpo.append("\nBienvenido al sistema!\n");
                break;
                
            case "ACTUALIZAR":
                cuerpo.append("Su cuenta ha sido actualizada en AgroSoft.\n");
                if (datosAdicionales != null) {
                    cuerpo.append("Detalles: ").append(datosAdicionales.toString()).append("\n");
                }
                break;
                
            case "ELIMINAR":
                cuerpo.append("Su cuenta ha sido eliminada del sistema AgroSoft.\n");
                cuerpo.append("Si cree que esto es un error, contacte al administrador.\n");
                break;
                
            default:
                cuerpo.append("Ha ocurrido un evento en su cuenta: ").append(evento).append("\n");
        }
        
        cuerpo.append("\nAtentamente,\n");
        cuerpo.append("Equipo AgroSoft");
        
        return cuerpo.toString();
    }
    
    @Override
    public String getNombre() {
        return "NotificacionEmailObserver";
    }
}

