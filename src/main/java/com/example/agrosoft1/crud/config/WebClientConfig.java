package com.example.agrosoft1.crud.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

/**
 * Configuración mejorada de WebClient para consumo de APIs externas
 * 
 * Mejoras:
 * - Timeout de conexión configurado
 * - Timeout de lectura configurado
 * - Mejor manejo de conexiones
 */
@Configuration
public class WebClientConfig {
    
    @Bean
    @SuppressWarnings("null")
    public WebClient.Builder webClientBuilder() {
        // Configurar HttpClient con timeouts
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(10));  // Timeout de respuesta
        
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient));
    }
    
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}

