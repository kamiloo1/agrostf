package com.example.agrosoft1.crud.dto;

import lombok.Data;

/**
 * DTO para representar datos del clima obtenidos de la API externa
 */
@Data
public class ClimaDTO {
    private String ciudad;
    private String pais;
    private Double temperatura;
    private Double temperaturaMinima;
    private Double temperaturaMaxima;
    private Double sensacionTermica;
    private String descripcion;
    private String icono; // Código del icono de OpenWeatherMap
    private Double humedad;
    private Double velocidadViento;
    private Double direccionViento; // En grados (0-360)
    private Double presion; // Presión atmosférica en hPa
    private Double visibilidad; // En metros
    private String unidadTemperatura = "°C";
    private String recomendacionAgricola; // Recomendación basada en el clima
}

