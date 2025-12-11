package com.example.agrosoft1.crud.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de propiedades personalizadas para el servicio de clima.
 * Elimina los warnings de propiedades desconocidas en application.properties.
 */
@Configuration
@ConfigurationProperties(prefix = "app.clima")
@SuppressWarnings("unused")
public class ClimaProperties {
    
    private String apiKey = "demo_key";
    private String apiUrl = "https://api.openweathermap.org/data/2.5";
    private int timeoutSeconds = 5;
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

