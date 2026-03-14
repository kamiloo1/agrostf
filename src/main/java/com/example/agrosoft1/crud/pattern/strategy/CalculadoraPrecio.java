package com.example.agrosoft1.crud.pattern.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contexto que utiliza una estrategia de cálculo de precio
 * 
 * PATRÓN STRATEGY (Comportamiento)
 * 
 * Propósito: Intercambiar algoritmos de cálculo de precio en tiempo de ejecución.
 * Permite cambiar la estrategia de cálculo sin modificar el código cliente.
 * 
 * Ubicación: pattern/strategy/
 * 
 * ¿Por qué se implementó?
 * - Permite cambiar la estrategia de cálculo de precio dinámicamente
 * - Facilita agregar nuevas estrategias (ej: descuento por temporada, descuento por volumen)
 * - Separa la lógica de cálculo del código cliente
 * - Facilita el testing: cada estrategia se puede probar independientemente
 * - Cumple con el principio Open/Closed: abierto para extensión, cerrado para modificación
 * 
 * Estrategias implementadas:
 * - CalculoPrecioNormal: Sin descuentos
 * - CalculoPrecioDescuento: Descuento del 10%
 * - CalculoPrecioMayorista: Descuento del 20%
 */
public class CalculadoraPrecio {
    
    private static final Logger logger = LoggerFactory.getLogger(CalculadoraPrecio.class);
    
    // La estrategia actual
    private EstrategiaCalculoPrecio estrategia;
    
    /**
     * Constructor que establece una estrategia por defecto
     */
    public CalculadoraPrecio() {
        this.estrategia = new CalculoPrecioNormal();
    }
    
    /**
     * Constructor que establece una estrategia específica
     * 
     * @param estrategia La estrategia a utilizar
     */
    public CalculadoraPrecio(EstrategiaCalculoPrecio estrategia) {
        this.estrategia = estrategia;
    }
    
    /**
     * Establece la estrategia de cálculo
     * 
     * @param estrategia La nueva estrategia a utilizar
     */
    public void setEstrategia(EstrategiaCalculoPrecio estrategia) {
        this.estrategia = estrategia;
        logger.info("Estrategia de cálculo cambiada a: {}", estrategia.getNombre());
    }
    
    /**
     * Calcula el precio final usando la estrategia actual
     * 
     * @param precioBase El precio base del producto/servicio
     * @return El precio final después de aplicar la estrategia
     */
    public double calcularPrecioFinal(double precioBase) {
        if (estrategia == null) {
            logger.warn("No hay estrategia establecida, usando precio normal");
            estrategia = new CalculoPrecioNormal();
        }
        
        double precioFinal = estrategia.calcularPrecio(precioBase);
        logger.debug("Precio base: {}, Estrategia: {}, Precio final: {}", 
            precioBase, estrategia.getNombre(), precioFinal);
        
        return precioFinal;
    }
    
    /**
     * Obtiene la estrategia actual
     * 
     * @return La estrategia actual
     */
    public EstrategiaCalculoPrecio getEstrategia() {
        return estrategia;
    }
    
    /**
     * Obtiene información sobre la estrategia actual
     * 
     * @return Un string con la información de la estrategia
     */
    public String getInfoEstrategia() {
        if (estrategia == null) {
            return "No hay estrategia establecida";
        }
        return String.format("%s: %s", estrategia.getNombre(), estrategia.getDescripcion());
    }
}

