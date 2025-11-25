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
    private String descripcion;
    private Double humedad;
    private Double velocidadViento;
    private String unidadTemperatura = "°C";
}

