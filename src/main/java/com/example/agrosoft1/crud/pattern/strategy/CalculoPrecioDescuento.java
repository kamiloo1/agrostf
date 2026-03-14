package com.example.agrosoft1.crud.pattern.strategy;

/**
 * Estrategia de cálculo de precio con descuento del 10%
 * 
 * Implementa EstrategiaCalculoPrecio para aplicar descuento del 10%
 */
public class CalculoPrecioDescuento implements EstrategiaCalculoPrecio {
    
    private static final double DESCUENTO_PORCENTAJE = 0.10; // 10%
    
    @Override
    public double calcularPrecio(double precioBase) {
        // Aplica descuento del 10%
        return precioBase * (1 - DESCUENTO_PORCENTAJE);
    }
    
    @Override
    public String getNombre() {
        return "Precio con Descuento";
    }
    
    @Override
    public String getDescripcion() {
        return "Precio con descuento del 10% aplicado";
    }
    
    /**
     * Obtiene el porcentaje de descuento
     * 
     * @return El porcentaje de descuento (10%)
     */
    public double getDescuentoPorcentaje() {
        return DESCUENTO_PORCENTAJE * 100;
    }
}

