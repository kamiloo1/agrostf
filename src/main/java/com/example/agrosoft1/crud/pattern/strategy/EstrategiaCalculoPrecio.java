package com.example.agrosoft1.crud.pattern.strategy;

/**
 * PATRÓN STRATEGY (Comportamiento)
 * 
 * Interfaz que define la estrategia de cálculo de precio
 * 
 * Propósito: Define una familia de algoritmos, los encapsula y los hace intercambiables.
 * Permite que el algoritmo varíe independientemente de los clientes que lo usan.
 */
public interface EstrategiaCalculoPrecio {
    
    /**
     * Calcula el precio final aplicando la estrategia correspondiente
     * 
     * @param precioBase El precio base del producto/servicio
     * @return El precio final después de aplicar la estrategia
     */
    double calcularPrecio(double precioBase);
    
    /**
     * Obtiene el nombre de la estrategia
     * 
     * @return El nombre de la estrategia
     */
    String getNombre();
    
    /**
     * Obtiene la descripción de la estrategia
     * 
     * @return La descripción
     */
    String getDescripcion();
}

