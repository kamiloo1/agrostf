package com.example.agrosoft1.crud.service;

import com.example.agrosoft1.crud.dto.ClimaDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * Servicio para consumo de Web Service externo - API del Clima.
 * 
 * Integra con OpenWeatherMap API para obtener información del clima actual.
 * 
 * URL: https://api.openweathermap.org/data/2.5/weather
 * Método HTTP: GET
 * 
 * Ejemplo de respuesta:
 * {
 *   "name": "Bogotá",
 *   "sys": {"country": "CO"},
 *   "main": {"temp": 15.5, "humidity": 75},
 *   "weather": [{"description": "nubes dispersas"}],
 *   "wind": {"speed": 3.2}
 * }
 */
@Service
public class ClimaService {
    
    private static final Logger logger = LoggerFactory.getLogger(ClimaService.class);
    
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    @Value("${app.clima.api.key:demo_key}")
    private String apiKey;
    
    @Value("${app.clima.api.url:https://api.openweathermap.org/data/2.5/weather}")
    private String apiUrl;
    
    public ClimaService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.baseUrl("https://api.openweathermap.org/data/2.5").build();
        this.objectMapper = objectMapper;
    }
    
    /**
     * Obtiene el clima actual de una ciudad
     * 
     * @param ciudad Nombre de la ciudad
     * @param codigoPais Código del país (opcional, ej: "CO" para Colombia)
     * @return ClimaDTO con la información del clima
     */
    public ClimaDTO obtenerClimaActual(String ciudad, String codigoPais) {
        try {
            // Construir query como final para uso en lambda
            final String query = (codigoPais != null && !codigoPais.isEmpty()) 
                    ? ciudad + "," + codigoPais 
                    : ciudad;
            
            logger.info("Consultando clima para: {}", query);
            
            // Si no hay API key configurada, retornar datos de ejemplo
            if ("demo_key".equals(apiKey)) {
                logger.warn("API key no configurada. Retornando datos de ejemplo.");
                return crearDatosEjemplo(ciudad, codigoPais);
            }
            
            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/weather")
                            .queryParam("q", query)
                            .queryParam("appid", apiKey)
                            .queryParam("units", "metric")
                            .queryParam("lang", "es")
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            return parsearRespuesta(response, ciudad, codigoPais);
            
        } catch (WebClientResponseException e) {
            logger.error("Error al consultar API del clima: {}", e.getMessage());
            // Retornar datos de ejemplo en caso de error
            return crearDatosEjemplo(ciudad, codigoPais);
        } catch (Exception e) {
            logger.error("Error inesperado al consultar clima: {}", e.getMessage());
            return crearDatosEjemplo(ciudad, codigoPais);
        }
    }
    
    /**
     * Parsea la respuesta JSON de la API
     */
    private ClimaDTO parsearRespuesta(String jsonResponse, String ciudad, String codigoPais) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            
            ClimaDTO clima = new ClimaDTO();
            clima.setCiudad(root.path("name").asText(ciudad));
            clima.setPais(root.path("sys").path("country").asText(codigoPais != null ? codigoPais : "CO"));
            
            // Temperatura (convertir de Kelvin a Celsius si es necesario)
            double temp = root.path("main").path("temp").asDouble(20.0);
            clima.setTemperatura(temp);
            
            // Descripción
            if (root.path("weather").isArray() && root.path("weather").size() > 0) {
                clima.setDescripcion(root.path("weather").get(0).path("description").asText("Despejado"));
            } else {
                clima.setDescripcion("Despejado");
            }
            
            // Humedad
            clima.setHumedad(root.path("main").path("humidity").asDouble(60.0));
            
            // Velocidad del viento
            clima.setVelocidadViento(root.path("wind").path("speed").asDouble(0.0));
            
            return clima;
        } catch (Exception e) {
            logger.error("Error al parsear respuesta: {}", e.getMessage());
            return crearDatosEjemplo(ciudad, codigoPais);
        }
    }
    
    /**
     * Crea datos de ejemplo cuando la API no está disponible
     */
    private ClimaDTO crearDatosEjemplo(String ciudad, String codigoPais) {
        ClimaDTO clima = new ClimaDTO();
        clima.setCiudad(ciudad != null ? ciudad : "Bogotá");
        clima.setPais(codigoPais != null ? codigoPais : "CO");
        clima.setTemperatura(18.5);
        clima.setDescripcion("Parcialmente nublado");
        clima.setHumedad(65.0);
        clima.setVelocidadViento(2.5);
        return clima;
    }
    
    /**
     * Obtiene el clima actual de Bogotá por defecto
     */
    public ClimaDTO obtenerClimaBogota() {
        return obtenerClimaActual("Bogotá", "CO");
    }
}

