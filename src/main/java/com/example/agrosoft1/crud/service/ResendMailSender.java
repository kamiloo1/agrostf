package com.example.agrosoft1.crud.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import jakarta.annotation.PostConstruct;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Envío por API HTTPS de Resend (necesario en Railway cuando SMTP 587 está bloqueado).
 */
@Service
public class ResendMailSender {

    private static final Logger log = LoggerFactory.getLogger(ResendMailSender.class);

    private final WebClient webClient;

    @Value("${app.resend.api.key:}")
    private String apiKey;

    @Value("${app.resend.from:}")
    private String from;

    public ResendMailSender(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.resend.com").build();
    }

    @PostConstruct
    void logModoCorreo() {
        if (isConfigured()) {
            log.info("Correo: modo Resend activo (RESEND_API_KEY / app.resend.api.key)");
        } else {
            log.warn("Correo: Resend no configurado; se usará SMTP. En Railway suele fallar Gmail (puerto 587 bloqueado). Añade RESEND_API_KEY.");
        }
    }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }

    private String effectiveFrom() {
        if (from != null && !from.isBlank()) {
            return from.trim();
        }
        return "onboarding@resend.dev";
    }

    public boolean enviar(String destinatario, String asunto, String mensajeTextoPlano) {
        if (!isConfigured()) {
            return false;
        }
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("from", effectiveFrom());
        body.put("to", List.of(destinatario));
        body.put("subject", asunto);
        body.put("text", mensajeTextoPlano);

        try {
            webClient.post()
                    .uri("/emails")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey.trim())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(25))
                    .block();
            log.info("Correo enviado vía Resend a {}", destinatario);
            return true;
        } catch (WebClientResponseException e) {
            log.error("Resend HTTP {}: {}", e.getStatusCode().value(), e.getResponseBodyAsString());
            return false;
        } catch (Exception e) {
            log.error("Error llamando a Resend: {}", e.getMessage(), e);
            return false;
        }
    }
}
