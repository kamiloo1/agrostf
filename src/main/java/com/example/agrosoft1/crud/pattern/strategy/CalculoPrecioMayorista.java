package com.example.agrosoft1.crud.pattern.strategy;

/**
 * Estrategia de cálculo de precio mayorista con descuento del 20%
 * 
 * Implementa EstrategiaCalculoPrecio para aplicar descuento mayorista del 20%
 */
public class CalculoPrecioMayorista implements EstrategiaCalculoPrecio {
    
    private static final double DESCUENTO_PORCENTAJE = 0.20; // 20%
    
    @Override
    public double calcularPrecio(double precioBase) {
        // Aplica descuento mayorista del 20%
        return precioBase * (1 - DESCUENTO_PORCENTAJE);
    }
    
    @Override
    public String getNombre() {
        return "Precio Mayorista";
    }
    
    @Override
    public String getDescripcion() {
        return "Precio con descuento mayorista del 20% aplicado";
    }
    
    /**
     * Obtiene el porcentaje de descuento
     * 
     * @return El porcentaje de descuento (20%)
     */
    public double getDescuentoPorcentaje() {
        return DESCUENTO_PORCENTAJE * 100;
    }
}

