package com.example.agrosoft1.crud.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio para envío masivo de correos electrónicos.
 * 
 * Permite enviar notificaciones masivas a múltiples destinatarios,
 * útil para alertas, campañas e informes de pacientes.
 */
@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    /**
     * Envía un correo a un único destinatario
     * 
     * @param destinatario Correo del destinatario
     * @param asunto Asunto del correo
     * @param mensaje Contenido del mensaje
     * @return true si se envió correctamente, false en caso contrario
     */
    public boolean enviarCorreo(String destinatario, String asunto, String mensaje) {
        if (mailSender == null) {
            logger.warn("JavaMailSender no configurado. Simulando envío de correo a: {}", destinatario);
            logger.info("Asunto: {}", asunto);
            logger.info("Mensaje: {}", mensaje);
            return true; // Simulación para desarrollo
        }
        
        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(destinatario);
            email.setSubject(asunto);
            email.setText(mensaje);
            email.setFrom("noreply@agrosoft.local");
            
            mailSender.send(email);
            logger.info("Correo enviado exitosamente a: {}", destinatario);
            return true;
        } catch (Exception e) {
            logger.error("Error al enviar correo a {}: {}", destinatario, e.getMessage());
            return false;
        }
    }
    
    /**
     * Envía correos masivos a múltiples destinatarios
     * 
     * @param destinatarios Lista de correos electrónicos
     * @param asunto Asunto del correo
     * @param mensaje Contenido del mensaje
     * @return Número de correos enviados exitosamente
     */
    public int enviarCorreosMasivos(List<String> destinatarios, String asunto, String mensaje) {
        if (destinatarios == null || destinatarios.isEmpty()) {
            logger.warn("Lista de destinatarios vacía");
            return 0;
        }
        
        int enviados = 0;
        int fallidos = 0;
        
        logger.info("Iniciando envío masivo a {} destinatarios", destinatarios.size());
        
        for (String destinatario : destinatarios) {
            if (destinatario != null && !destinatario.trim().isEmpty()) {
                boolean enviado = enviarCorreo(destinatario.trim(), asunto, mensaje);
                if (enviado) {
                    enviados++;
                } else {
                    fallidos++;
                }
            }
        }
        
        logger.info("Envío masivo completado: {} exitosos, {} fallidos", enviados, fallidos);
        return enviados;
    }
    
    /**
     * Envía notificación de alerta
     * 
     * @param destinatarios Lista de correos
     * @param tipoAlerta Tipo de alerta (ej: "Tratamiento pendiente", "Vacunación requerida")
     * @param detalle Detalle de la alerta
     * @return Número de correos enviados
     */
    public int enviarAlerta(List<String> destinatarios, String tipoAlerta, String detalle) {
        String asunto = "🚨 Alerta AgroSoft: " + tipoAlerta;
        String mensaje = String.format(
            "Estimado usuario,\n\n" +
            "Se ha generado una alerta en el sistema AgroSoft:\n\n" +
            "Tipo: %s\n" +
            "Detalle: %s\n\n" +
            "Por favor, revise el sistema para más información.\n\n" +
            "Saludos,\n" +
            "Sistema AgroSoft",
            tipoAlerta, detalle
        );
        return enviarCorreosMasivos(destinatarios, asunto, mensaje);
    }
    
    /**
     * Envía informe de pacientes
     * 
     * @param destinatarios Lista de correos
     * @param resumen Resumen del informe
     * @return Número de correos enviados
     */
    public int enviarInformePacientes(List<String> destinatarios, String resumen) {
        String asunto = "📊 Informe de Pacientes - AgroSoft";
        String mensaje = String.format(
            "Estimado usuario,\n\n" +
            "Se adjunta el informe de pacientes del sistema AgroSoft:\n\n" +
            "%s\n\n" +
            "Saludos,\n" +
            "Sistema AgroSoft",
            resumen
        );
        return enviarCorreosMasivos(destinatarios, asunto, mensaje);
    }
    
    /**
     * Envía campaña promocional o informativa
     * 
     * @param destinatarios Lista de correos
     * @param titulo Título de la campaña
     * @param contenido Contenido de la campaña
     * @return Número de correos enviados
     */
    public int enviarCampana(List<String> destinatarios, String titulo, String contenido) {
        String asunto = "📢 Campaña AgroSoft: " + titulo;
        String mensaje = String.format(
            "Estimado usuario,\n\n" +
            "%s\n\n" +
            "%s\n\n" +
            "Saludos,\n" +
            "Equipo AgroSoft",
            titulo, contenido
        );
        return enviarCorreosMasivos(destinatarios, asunto, mensaje);
    }
}

