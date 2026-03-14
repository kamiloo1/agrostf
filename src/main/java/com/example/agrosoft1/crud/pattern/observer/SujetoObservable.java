package com.example.agrosoft1.crud.pattern.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * PATRÓN OBSERVER (Comportamiento)
 * 
 * Clase que mantiene una lista de observadores y los notifica de cambios
 * 
 * Propósito: Notificar cambios a múltiples objetos automáticamente.
 * Cuando el estado del sujeto cambia, todos los observadores registrados
 * son notificados automáticamente.
 * 
 * Ubicación: pattern/observer/SujetoObservable.java
 * 
 * ¿Por qué se implementó?
 * - Permite notificar a múltiples componentes cuando ocurre un evento (crear, actualizar, eliminar usuario)
 * - Desacopla el sujeto de los observadores: el sujeto no necesita conocer detalles de los observadores
 * - Facilita agregar nuevos observadores sin modificar código existente
 * - Útil para sistemas de notificaciones, logging, auditoría, etc.
 * - Permite que diferentes componentes reaccionen a los mismos eventos de forma independiente
 * 
 * Componentes:
 * - Observador (interfaz): Define el contrato para los observadores
 * - SujetoObservable (esta clase): Mantiene lista de observadores y los notifica
 * - NotificacionEmailObserver: Observador concreto que envía emails
 * 
 * Uso: Notifica cuando se crea, actualiza o elimina un usuario
 */
public class SujetoObservable {
    
    private static final Logger logger = LoggerFactory.getLogger(SujetoObservable.class);
    
    // Lista de observadores registrados
    private final List<Observador> observadores;
    
    /**
     * Constructor que inicializa la lista de observadores
     */
    public SujetoObservable() {
        this.observadores = new ArrayList<>();
    }
    
    /**
     * Registra un nuevo observador
     * 
     * @param observador El observador a registrar
     */
    public void agregarObservador(Observador observador) {
        synchronized (observadores) {
            if (!observadores.contains(observador)) {
                observadores.add(observador);
                logger.info("Observador registrado: {}", observador.getNombre());
            }
        }
    }
    
    /**
     * Elimina un observador de la lista
     * 
     * @param observador El observador a eliminar
     */
    public void eliminarObservador(Observador observador) {
        synchronized (observadores) {
            boolean removido = observadores.remove(observador);
            if (removido) {
                logger.info("Observador eliminado: {}", observador.getNombre());
            }
        }
    }
    
    /**
     * Notifica a todos los observadores sobre un evento
     * 
     * @param evento El tipo de evento (CREAR, ACTUALIZAR, ELIMINAR)
     * @param usuario El usuario afectado por el evento
     * @param datosAdicionales Datos adicionales sobre el evento
     */
    public void notificarObservadores(String evento, com.example.agrosoft1.crud.entity.Usuario usuario, Object datosAdicionales) {
        logger.info("Notificando {} observadores sobre evento: {}", observadores.size(), evento);
        
        // Crear una copia de la lista para evitar problemas de concurrencia
        List<Observador> observadoresCopia;
        synchronized (observadores) {
            observadoresCopia = new ArrayList<>(observadores);
        }
        
        // Notificar a cada observador
        for (Observador observador : observadoresCopia) {
            try {
                observador.actualizar(evento, usuario, datosAdicionales);
            } catch (Exception e) {
                logger.error("Error al notificar al observador {}: {}", 
                    observador.getNombre(), e.getMessage(), e);
            }
        }
    }
    
    /**
     * Obtiene el número de observadores registrados
     * 
     * @return El número de observadores
     */
    public int getNumeroObservadores() {
        return observadores.size();
    }
    
    /**
     * Limpia todos los observadores
     */
    public void limpiarObservadores() {
        synchronized (observadores) {
            observadores.clear();
            logger.info("Todos los observadores han sido eliminados");
        }
    }
}

