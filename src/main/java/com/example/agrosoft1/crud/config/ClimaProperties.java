package com.example.agrosoft1.crud.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuración del servicio de clima (OpenWeatherMap).
 * Las propiedades en application.properties con prefijo app.clima se enlazan aquí.
 * Nombres en .properties: app.clima.api.key, app.clima.api.url, app.clima.timeout.seconds, app.clima.cache.enabled
 */
@Component
@ConfigurationProperties(prefix = "app.clima")
public class ClimaProperties {

    /** API Key de OpenWeatherMap (demo_key = datos de ejemplo) */
    private String apiKey = "demo_key";
    /** URL base de la API (ej. https://api.openweathermap.org/data/2.5) */
    private String apiUrl = "https://api.openweathermap.org/data/2.5";
    /** Timeout en segundos para peticiones HTTP */
    private int timeoutSeconds = 5;
    /** Habilitar cache de respuestas */
    private boolean cacheEnabled = true;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }
}

