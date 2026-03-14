package com.example.agrosoft1.crud.pattern.observer;

import com.example.agrosoft1.crud.entity.Usuario;

/**
 * PATRÓN OBSERVER (Comportamiento)
 * 
 * Interfaz que define el contrato para los observadores
 * 
 * Propósito: Define una dependencia uno-a-muchos entre objetos, de manera que
 * cuando un objeto cambia de estado, todos sus dependientes son notificados
 * y actualizados automáticamente.
 */
public interface Observador {
    
    /**
     * Método llamado cuando el sujeto observable notifica un cambio
     * 
     * @param evento El tipo de evento que ocurrió
     * @param usuario El usuario afectado por el evento
     * @param datosAdicionales Datos adicionales sobre el evento (opcional)
     */
    void actualizar(String evento, Usuario usuario, Object datosAdicionales);
    
    /**
     * Obtiene el nombre del observador
     * 
     * @return El nombre del observador
     */
    String getNombre();
}

