package com.example.agrosoft1.crud.pattern.factory;

import com.example.agrosoft1.crud.pattern.factory.reporte.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PATRÓN FACTORY (Creacional)
 * 
 * Propósito: Crear diferentes tipos de reportes sin exponer la lógica de creación
 * al cliente. Centraliza la creación de objetos relacionados.
 * 
 * Características implementadas:
 * - Método estático crearReporte() que retorna la implementación correcta
 * - Encapsula la lógica de decisión de qué tipo de reporte crear
 * - Facilita agregar nuevos tipos de reportes sin modificar código existente
 * - Separa la creación de objetos de su uso
 * 
 * Ubicación: pattern/factory/ReporteFactory.java
 * 
 * ¿Por qué se implementó?
 * - Permite crear diferentes tipos de reportes (Cultivo, Ganado, General) de forma uniforme
 * - Facilita la extensión: agregar nuevos tipos de reportes es simple
 * - Oculta la complejidad de creación de objetos al cliente
 * - Centraliza la lógica de decisión sobre qué reporte crear
 * - Reduce el acoplamiento entre el código cliente y las clases concretas
 * 
 * Tipos de reportes que crea:
 * - ReporteCultivoService: Reportes específicos de cultivos
 * - ReporteGanadoService: Reportes específicos de ganado
 * - ReporteGeneralService: Reportes generales del sistema
 */
public class ReporteFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(ReporteFactory.class);
    
    /**
     * Tipos de reportes disponibles
     */
    public enum TipoReporte {
        CULTIVO,
        GANADO,
        GENERAL
    }
    
    /**
     * Crea una instancia del servicio de reporte según el tipo especificado
     * 
     * @param tipo El tipo de reporte a crear
     * @return Una instancia del servicio de reporte correspondiente
     * @throws IllegalArgumentException si el tipo no es válido
     */
    public static ReporteService crearReporte(TipoReporte tipo) {
        logger.info("Creando reporte de tipo: {}", tipo);
        
        switch (tipo) {
            case CULTIVO:
                return new ReporteCultivoService();
                
            case GANADO:
                return new ReporteGanadoService();
                
            case GENERAL:
                return new ReporteGeneralService();
                
            default:
                logger.error("Tipo de reporte no válido: {}", tipo);
                throw new IllegalArgumentException("Tipo de reporte no válido: " + tipo);
        }
    }
    
    /**
     * Crea un reporte a partir de una cadena de texto
     * 
     * @param tipoStr El tipo de reporte como cadena ("CULTIVO", "GANADO", "GENERAL")
     * @return Una instancia del servicio de reporte correspondiente
     */
    public static ReporteService crearReporte(String tipoStr) {
        try {
            TipoReporte tipo = TipoReporte.valueOf(tipoStr.toUpperCase());
            return crearReporte(tipo);
        } catch (IllegalArgumentException e) {
            logger.error("Tipo de reporte no reconocido: {}", tipoStr);
            throw new IllegalArgumentException("Tipo de reporte no reconocido: " + tipoStr, e);
        }
    }
}

