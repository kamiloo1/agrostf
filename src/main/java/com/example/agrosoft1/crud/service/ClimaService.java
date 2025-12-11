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
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio mejorado para consumo de Web Service externo - API del Clima.
 * 
 * Mejoras implementadas:
 * - ✅ Cache con expiración (10 minutos)
 * - ✅ Timeout configurable (5 segundos)
 * - ✅ Retry logic (3 intentos con backoff exponencial)
 * - ✅ Validación de parámetros
 * - ✅ Mejor manejo de errores
 * - ✅ Más información del clima (presión, sensación térmica, etc.)
 * 
 * Integra con OpenWeatherMap API para obtener información del clima actual.
 * 
 * URL: https://api.openweathermap.org/data/2.5/weather
 * Método HTTP: GET
 */
@Service
public class ClimaService {
    
    private static final Logger logger = LoggerFactory.getLogger(ClimaService.class);
    
    private WebClient webClient;
    private final ObjectMapper objectMapper;
    
    // Cache simple con expiración (10 minutos)
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private static final long CACHE_EXPIRATION_MINUTES = 10;
    
    @Value("${app.clima.api.key:demo_key}")
    private String apiKey;
    
    @Value("${app.clima.api.url:https://api.openweathermap.org/data/2.5}")
    private String apiBaseUrl;
    
    @Value("${app.clima.timeout.seconds:5}")
    private int timeoutSeconds;
    
    @Value("${app.clima.cache.enabled:true}")
    private boolean cacheEnabled;
    
    public ClimaService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        // WebClient se inicializará en @PostConstruct
    }
    
    @jakarta.annotation.PostConstruct
    public void init() {
        // Configurar WebClient después de que se inyecten los valores
        this.webClient = WebClient.builder()
                .baseUrl(apiBaseUrl)
                .build();
        logger.info("WebClient configurado con URL base: {}", apiBaseUrl);
        logger.info("API Key configurada: {} (demo_key = datos de ejemplo)", 
            apiKey.equals("demo_key") ? "demo_key (datos de ejemplo)" : "***configurada***");
    }
    
    /**
     * Clase interna para el cache
     */
    private static class CacheEntry {
        ClimaDTO clima;
        LocalDateTime timestamp;
        
        CacheEntry(ClimaDTO clima) {
            this.clima = clima;
            this.timestamp = LocalDateTime.now();
        }
        
        boolean isExpired() {
            return LocalDateTime.now().isAfter(
                timestamp.plusMinutes(CACHE_EXPIRATION_MINUTES)
            );
        }
    }
    
    /**
     * Obtiene el clima actual de una ciudad (versión mejorada)
     * 
     * Mejoras:
     * - Validación de parámetros
     * - Cache con expiración
     * - Timeout configurable
     * - Retry logic (3 intentos)
     * - Mejor manejo de errores
     * 
     * @param ciudad Nombre de la ciudad
     * @param codigoPais Código del país (opcional, ej: "CO" para Colombia)
     * @return ClimaDTO con la información del clima
     */
    public ClimaDTO obtenerClimaActual(String ciudad, String codigoPais) {
        return obtenerClimaActual(ciudad, codigoPais, false);
    }
    
    /**
     * Obtiene el clima actual de una ciudad con opción de forzar actualización
     * 
     * @param ciudad Nombre de la ciudad
     * @param codigoPais Código del país (opcional, ej: "CO" para Colombia)
     * @param forzarActualizacion Si es true, ignora el cache y consulta la API
     * @return ClimaDTO con la información del clima
     */
    public ClimaDTO obtenerClimaActual(String ciudad, String codigoPais, boolean forzarActualizacion) {
        try {
            // Validación de parámetros
            if (ciudad == null || ciudad.trim().isEmpty()) {
                logger.warn("Ciudad no especificada, usando Bogotá por defecto");
                ciudad = "Bogotá";
            }
            
            // Normalizar ciudad (trim y capitalizar primera letra)
            ciudad = ciudad.trim();
            if (!ciudad.isEmpty()) {
                ciudad = ciudad.substring(0, 1).toUpperCase() + ciudad.substring(1).toLowerCase();
            }
            
            // Construir query como final para uso en lambda
            final String query = (codigoPais != null && !codigoPais.isEmpty()) 
                    ? ciudad + "," + codigoPais 
                    : ciudad;
            
            final String cacheKey = query.toLowerCase();
            
            // SIEMPRE generar datos de ejemplo si la API key es demo_key
            if ("demo_key".equals(apiKey) || apiKey == null || apiKey.isEmpty()) {
                logger.info("Usando datos de ejemplo para: {} (API key: demo_key)", query);
                
                // Si se fuerza actualización o no hay cache, generar nuevos datos
                if (forzarActualizacion || !cacheEnabled) {
                    ClimaDTO ejemplo = crearDatosEjemplo(ciudad, codigoPais);
                    if (cacheEnabled) {
                        cache.put(cacheKey, new CacheEntry(ejemplo));
                    }
                    return ejemplo;
                }
                
                // Verificar cache
                CacheEntry cached = cache.get(cacheKey);
                if (cached != null && !cached.isExpired()) {
                    logger.info("Retornando datos de ejemplo desde cache para: {}", query);
                    return cached.clima;
                } else {
                    // Generar nuevos datos de ejemplo
                    ClimaDTO ejemplo = crearDatosEjemplo(ciudad, codigoPais);
                    if (cacheEnabled) {
                        cache.put(cacheKey, new CacheEntry(ejemplo));
                    }
                    return ejemplo;
                }
            }
            
            // Verificar cache (solo si no se fuerza la actualización y hay API key real)
            if (cacheEnabled && !forzarActualizacion) {
                CacheEntry cached = cache.get(cacheKey);
                if (cached != null && !cached.isExpired()) {
                    logger.info("Retornando clima desde cache para: {}", query);
                    return cached.clima;
                } else if (cached != null && cached.isExpired()) {
                    logger.info("Cache expirado para: {}, consultando API", query);
                    cache.remove(cacheKey);
                }
            } else if (forzarActualizacion) {
                logger.info("Forzando actualización del clima para: {} (ignorando cache)", query);
                cache.remove(cacheKey);
            }
            
            logger.info("Consultando clima para: {} (timeout: {}s)", query, timeoutSeconds);
            
            // Validar que webClient esté inicializado
            if (webClient == null) {
                logger.error("WebClient no inicializado. Inicializando ahora...");
                this.webClient = WebClient.builder()
                        .baseUrl(apiBaseUrl)
                        .build();
            }
            
            logger.info("Consultando API del clima: {}/weather?q={}&appid={}...", 
                apiBaseUrl, query, apiKey.equals("demo_key") ? "demo_key" : "***");
            
            // Petición con timeout y retry
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
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                            .filter(throwable -> throwable instanceof WebClientResponseException 
                                    && ((WebClientResponseException) throwable).getStatusCode().is5xxServerError())
                            .doBeforeRetry(retrySignal -> 
                                logger.warn("Reintentando consulta de clima (intento {}/3)", 
                                    retrySignal.totalRetries() + 1)))
                    .block();
            
            if (response == null || response.trim().isEmpty()) {
                throw new RuntimeException("Respuesta vacía de la API del clima");
            }
            
            logger.debug("Respuesta recibida de la API (primeros 200 caracteres): {}", 
                response.length() > 200 ? response.substring(0, 200) + "..." : response);
            
            ClimaDTO clima = parsearRespuesta(response, ciudad, codigoPais);
            
            // Guardar en cache
            if (cacheEnabled && clima != null) {
                cache.put(cacheKey, new CacheEntry(clima));
                logger.info("Clima obtenido y guardado en cache para: {}", query);
            }
            
            return clima;
            
        } catch (WebClientResponseException e) {
            logger.warn("Error HTTP al consultar API del clima ({}): {}. Retornando datos de ejemplo.", 
                e.getStatusCode(), e.getMessage());
            ClimaDTO ejemplo = crearDatosEjemplo(ciudad, codigoPais);
            if (cacheEnabled) {
                cache.put(cacheKey, new CacheEntry(ejemplo));
            }
            return ejemplo;
            
        } catch (Exception e) {
            // Manejar timeout y otros errores - SIEMPRE retornar datos de ejemplo
            String errorMsg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            logger.warn("Error al consultar clima: {}. Retornando datos de ejemplo.", errorMsg);
            
            // SIEMPRE retornar datos de ejemplo en caso de error
            ClimaDTO ejemplo = crearDatosEjemplo(ciudad, codigoPais);
            if (cacheEnabled) {
                cache.put(cacheKey, new CacheEntry(ejemplo));
            }
            return ejemplo;
        }
    }
    
    /**
     * Parsea la respuesta JSON de la API (versión mejorada con más datos)
     */
    private ClimaDTO parsearRespuesta(String jsonResponse, String ciudad, String codigoPais) {
        try {
            if (jsonResponse == null || jsonResponse.trim().isEmpty()) {
                logger.error("Respuesta JSON vacía o nula");
                throw new RuntimeException("Respuesta JSON vacía");
            }
            
            JsonNode root = objectMapper.readTree(jsonResponse);
            
            // Verificar si hay error en la respuesta
            if (root.has("cod") && root.path("cod").asInt() != 200) {
                String message = root.path("message").asText("Error desconocido");
                logger.error("Error en respuesta de API: código {}, mensaje: {}", 
                    root.path("cod").asInt(), message);
                throw new RuntimeException("Error de API: " + message);
            }
            
            ClimaDTO clima = new ClimaDTO();
            clima.setCiudad(root.path("name").asText(ciudad));
            clima.setPais(root.path("sys").path("country").asText(codigoPais != null ? codigoPais : "CO"));
            
            JsonNode main = root.path("main");
            
            // Temperatura
            double temp = main.path("temp").asDouble(20.0);
            clima.setTemperatura(temp);
            
            // Sensación térmica (feels_like)
            if (main.has("feels_like")) {
                clima.setSensacionTermica(main.path("feels_like").asDouble());
            }
            
            // Presión atmosférica
            if (main.has("pressure")) {
                clima.setPresion(main.path("pressure").asDouble());
            }
            
            // Temperatura mínima y máxima
            if (main.has("temp_min")) {
                clima.setTemperaturaMinima(main.path("temp_min").asDouble());
            }
            if (main.has("temp_max")) {
                clima.setTemperaturaMaxima(main.path("temp_max").asDouble());
            }
            
            // Descripción e icono
            if (root.path("weather").isArray() && root.path("weather").size() > 0) {
                JsonNode weather = root.path("weather").get(0);
                clima.setDescripcion(weather.path("description").asText("Despejado"));
                
                // Icono del clima
                if (weather.has("icon")) {
                    clima.setIcono(weather.path("icon").asText());
                }
            } else {
                clima.setDescripcion("Despejado");
            }
            
            // Humedad
            clima.setHumedad(main.path("humidity").asDouble(60.0));
            
            // Velocidad del viento
            JsonNode wind = root.path("wind");
            clima.setVelocidadViento(wind.path("speed").asDouble(0.0));
            
            // Dirección del viento (si está disponible)
            if (wind.has("deg")) {
                clima.setDireccionViento(wind.path("deg").asDouble());
            }
            
            // Visibilidad (si está disponible)
            if (root.has("visibility")) {
                clima.setVisibilidad(root.path("visibility").asDouble() / 1000.0); // Convertir a km
            }
            
            // Generar recomendación agrícola basada en el clima
            clima.setRecomendacionAgricola(generarRecomendacionAgricola(clima));
            
            logger.info("Clima parseado correctamente para {}: {}°C, {}", 
                clima.getCiudad(), clima.getTemperatura(), clima.getDescripcion());
            
            return clima;
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            logger.error("Error al parsear JSON de la API: {}", e.getMessage());
            logger.debug("JSON recibido (primeros 500 caracteres): {}", 
                jsonResponse != null && jsonResponse.length() > 500 
                    ? jsonResponse.substring(0, 500) + "..." 
                    : jsonResponse);
            throw new RuntimeException("Error al procesar respuesta JSON de la API: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error inesperado al parsear respuesta: {}", e.getMessage(), e);
            throw new RuntimeException("Error al procesar respuesta de la API", e);
        }
    }
    
    /**
     * Limpia el cache expirado (método de utilidad)
     */
    public void limpiarCacheExpirado() {
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
        logger.info("Cache limpiado. Entradas restantes: {}", cache.size());
    }
    
    /**
     * Obtiene estadísticas del cache
     */
    public Map<String, Object> obtenerEstadisticasCache() {
        long expiradas = cache.values().stream()
                .filter(CacheEntry::isExpired)
                .count();
        
        return Map.of(
            "total", cache.size(),
            "expiradas", expiradas,
            "activas", cache.size() - expiradas,
            "tiempoExpiracionMinutos", CACHE_EXPIRATION_MINUTES
        );
    }
    
    /**
     * Genera recomendaciones agrícolas basadas en las condiciones del clima
     */
    private String generarRecomendacionAgricola(ClimaDTO clima) {
        StringBuilder recomendaciones = new StringBuilder();
        
        // Recomendaciones basadas en temperatura
        if (clima.getTemperatura() != null) {
            if (clima.getTemperatura() < 10) {
                recomendaciones.append("⚠️ Temperatura baja: Proteger cultivos sensibles al frío. ");
            } else if (clima.getTemperatura() > 30) {
                recomendaciones.append("🌡️ Temperatura alta: Aumentar riego y sombra para cultivos. ");
            } else {
                recomendaciones.append("✅ Temperatura óptima para la mayoría de cultivos. ");
            }
        }
        
        // Recomendaciones basadas en humedad
        if (clima.getHumedad() != null) {
            if (clima.getHumedad() < 40) {
                recomendaciones.append("💧 Humedad baja: Considerar riego adicional. ");
            } else if (clima.getHumedad() > 80) {
                recomendaciones.append("🌧️ Humedad alta: Vigilar enfermedades fúngicas. ");
            }
        }
        
        // Recomendaciones basadas en viento
        if (clima.getVelocidadViento() != null && clima.getVelocidadViento() > 15) {
            recomendaciones.append("💨 Viento fuerte: Proteger estructuras y cultivos frágiles. ");
        }
        
        // Recomendaciones basadas en descripción
        if (clima.getDescripcion() != null) {
            String desc = clima.getDescripcion().toLowerCase();
            if (desc.contains("lluvia") || desc.contains("rain")) {
                recomendaciones.append("🌧️ Lluvia esperada: Evitar aplicaciones foliares. ");
            } else if (desc.contains("soleado") || desc.contains("clear")) {
                recomendaciones.append("☀️ Día soleado: Ideal para actividades al aire libre. ");
            }
        }
        
        return recomendaciones.length() > 0 
            ? recomendaciones.toString().trim() 
            : "Condiciones normales para actividades agrícolas.";
    }
    
    /**
     * Crea datos de ejemplo cuando la API no está disponible
     * Los datos varían según la ciudad y la hora actual para simular diferentes condiciones
     */
    private ClimaDTO crearDatosEjemplo(String ciudad, String codigoPais) {
        ClimaDTO clima = new ClimaDTO();
        clima.setCiudad(ciudad != null ? ciudad : "Bogotá");
        clima.setPais(codigoPais != null ? codigoPais : "CO");
        
        // Generar datos variables basados en el nombre de la ciudad Y la hora actual
        // Esto asegura que los datos cambien incluso para la misma ciudad
        int hashCiudad = (ciudad != null ? ciudad.hashCode() : 0);
        int hashHora = LocalDateTime.now().getHour(); // Hora actual (0-23)
        int hashCombinado = hashCiudad + hashHora * 31; // Combinar ambos hashes
        
        // Temperatura varía según ciudad y hora (más cálido durante el día)
        double baseTemp = 15.0 + (Math.abs(hashCiudad) % 12) + (hashHora > 6 && hashHora < 18 ? 5 : 0);
        double variacion = (Math.abs(hashCombinado) % 8) - 4; // Variación de -4 a +4
        
        clima.setTemperatura(Math.max(10, Math.min(35, baseTemp + variacion))); // Entre 10-35°C
        clima.setTemperaturaMinima(Math.max(5, clima.getTemperatura() - 4.0));
        clima.setTemperaturaMaxima(Math.min(40, clima.getTemperatura() + 6.0));
        clima.setSensacionTermica(clima.getTemperatura() + (variacion * 0.1));
        
        // Descripciones variadas según el hash combinado
        String[] descripciones = {
            "Parcialmente nublado", "Despejado", "Nublado", 
            "Lluvia ligera", "Soleado", "Bruma", "Viento moderado", "Lluvia moderada"
        };
        clima.setDescripcion(descripciones[Math.abs(hashCombinado) % descripciones.length]);
        
        // Icono correspondiente a la descripción
        String[] iconos = {"02d", "01d", "03d", "10d", "01d", "50d", "03d", "09d"};
        clima.setIcono(iconos[Math.abs(hashCombinado) % iconos.length]);
        
        clima.setHumedad(45.0 + (Math.abs(hashCombinado) % 45)); // Humedad entre 45-90%
        clima.setVelocidadViento(0.5 + (Math.abs(hashCombinado) % 12)); // Viento entre 0.5-12.5 m/s
        clima.setPresion(995.0 + (Math.abs(hashCombinado) % 35)); // Presión entre 995-1030 hPa
        clima.setVisibilidad(3.0 + (Math.abs(hashCombinado) % 17)); // Visibilidad entre 3-20 km
        
        if (Math.abs(hashCombinado) % 2 == 0) {
            clima.setDireccionViento((double)(Math.abs(hashCombinado) % 360)); // Dirección 0-360°
        }
        
        // Generar recomendación basada en los datos
        clima.setRecomendacionAgricola(generarRecomendacionAgricola(clima));
        
        logger.info("Datos de ejemplo generados para {} (hora: {}): {}°C, {}", 
            clima.getCiudad(), hashHora, clima.getTemperatura(), clima.getDescripcion());
        
        return clima;
    }
    
    /**
     * Obtiene el clima actual de Bogotá por defecto
     */
    public ClimaDTO obtenerClimaBogota() {
        return obtenerClimaActual("Bogotá", "CO");
    }
}

