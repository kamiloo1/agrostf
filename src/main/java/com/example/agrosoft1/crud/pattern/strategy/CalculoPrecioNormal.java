package com.example.agrosoft1.crud.pattern.strategy;

/**
 * Estrategia de cálculo de precio normal (sin descuentos)
 * 
 * Implementa EstrategiaCalculoPrecio para precios sin descuento
 */
public class CalculoPrecioNormal implements EstrategiaCalculoPrecio {
    
    @Override
    public double calcularPrecio(double precioBase) {
        // Sin descuento, retorna el precio base
        return precioBase;
    }
    
    @Override
    public String getNombre() {
        return "Precio Normal";
    }
    
    @Override
    public String getDescripcion() {
        return "Precio sin descuentos aplicados";
    }
}

