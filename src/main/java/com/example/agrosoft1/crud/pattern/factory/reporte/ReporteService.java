package com.example.agrosoft1.crud.pattern.factory.reporte;

import java.util.Map;

/**
 * Interfaz común para todos los servicios de reporte
 * Define el contrato que deben cumplir todos los tipos de reportes
 */
public interface ReporteService {
    
    /**
     * Genera el reporte correspondiente
     * 
     * @return Un mapa con los datos del reporte
     */
    Map<String, Object> generarReporte();
    
    /**
     * Obtiene el nombre del tipo de reporte
     * 
     * @return El nombre del reporte
     */
    String getNombreReporte();
    
    /**
     * Obtiene la descripción del reporte
     * 
     * @return La descripción
     */
    String getDescripcion();
}

