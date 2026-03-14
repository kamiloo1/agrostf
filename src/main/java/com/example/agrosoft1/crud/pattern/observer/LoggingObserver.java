package com.example.agrosoft1.crud.pattern.observer;

import com.example.agrosoft1.crud.entity.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Observador concreto que registra eventos en el log
 * 
 * Implementa la interfaz Observador para registrar eventos relacionados
 * con usuarios en el sistema de logging
 */
@Component
public class LoggingObserver implements Observador {
    
    private static final Logger logger = LoggerFactory.getLogger(LoggingObserver.class);
    
    @Override
    public void actualizar(String evento, Usuario usuario, Object datosAdicionales) {
        if (usuario == null) {
            logger.warn("Evento {} recibido con usuario nulo", evento);
            return;
        }
        
        String mensaje = String.format(
            "EVENTO: %s | Usuario: %s (ID: %d, Email: %s)",
            evento.toUpperCase(),
            usuario.getNombre() != null ? usuario.getNombre() : "N/A",
            usuario.getId() != null ? usuario.getId() : -1,
            usuario.getCorreo() != null ? usuario.getCorreo() : "N/A"
        );
        
        switch (evento.toUpperCase()) {
            case "CREAR":
                logger.info("✅ {}", mensaje);
                break;
            case "ACTUALIZAR":
                logger.info("🔄 {}", mensaje);
                break;
            case "ELIMINAR":
                logger.warn("🗑️ {}", mensaje);
                break;
            default:
                logger.info("📝 {}", mensaje);
        }
        
        if (datosAdicionales != null) {
            logger.debug("Datos adicionales: {}", datosAdicionales);
        }
    }
    
    @Override
    public String getNombre() {
        return "LoggingObserver";
    }
}

