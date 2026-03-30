package com.example.agrosoft1.crud.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Despacho de correo fuera del hilo HTTP usando el executor {@code mailExecutor}.
 */
@Service
public class MailDispatchService {

    private static final Logger logger = LoggerFactory.getLogger(MailDispatchService.class);

    private final EmailService emailService;

    public MailDispatchService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Async("mailExecutor")
    public void enviarCorreosMasivosAsync(List<String> destinatarios, String asunto, String mensaje) {
        try {
            emailService.enviarCorreosMasivos(destinatarios, asunto, mensaje);
        } catch (Exception e) {
            logger.error("Error en envío asíncrono masivo: {}", e.getMessage(), e);
        }
    }

    @Async("mailExecutor")
    public void enviarCorreoAsync(String destinatario, String asunto, String mensaje) {
        try {
            emailService.enviarCorreo(destinatario, asunto, mensaje);
        } catch (Exception e) {
            logger.error("Error en envío asíncrono simple: {}", e.getMessage(), e);
        }
    }
}
