package com.example.agrosoft1.crud.pattern.singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * PATRÓN SINGLETON (Creacional)
 * 
 * Propósito: Asegurar que existe una única instancia de configuración del sistema
 * en toda la aplicación, proporcionando un punto de acceso global.
 * 
 * Características implementadas:
 * - Constructor privado para prevenir instanciación directa
 * - Método estático getInstancia() para obtener la única instancia
 * - Sincronizado para thread-safety (evita problemas en aplicaciones multi-hilo)
 * - Lazy initialization (se crea solo cuando se necesita)
 * 
 * Ubicación: pattern/singleton/ConfiguracionSingleton.java
 * 
 * ¿Por qué se implementó?
 * - Centraliza la configuración del sistema en un solo lugar
 * - Evita múltiples instancias que podrían causar inconsistencias
 * - Facilita el acceso global a configuraciones sin pasar parámetros
 * - Útil para configuraciones que deben ser compartidas por toda la aplicación
 * - Thread-safe para aplicaciones concurrentes
 */
public class ConfiguracionSingleton {
    
    private static final Logger logger = LoggerFactory.getLogger(ConfiguracionSingleton.class);
    
    // Instancia única (volatile para garantizar visibilidad en threads)
    private static volatile ConfiguracionSingleton instancia;
    
    // Configuraciones almacenadas como mapa clave-valor
    private final Map<String, Object> configuraciones;
    
    /**
     * Constructor privado para prevenir instanciación directa
     */
    private ConfiguracionSingleton() {
        this.configuraciones = new HashMap<>();
        inicializarConfiguraciones();
        logger.info("ConfiguracionSingleton inicializado");
    }
    
    /**
     * Método estático sincronizado para obtener la única instancia
     * Usa double-checked locking para mejorar el rendimiento
     * 
     * @return La única instancia de ConfiguracionSingleton
     */
    public static ConfiguracionSingleton getInstancia() {
        if (instancia == null) {
            synchronized (ConfiguracionSingleton.class) {
                if (instancia == null) {
                    instancia = new ConfiguracionSingleton();
                }
            }
        }
        return instancia;
    }
    
    /**
     * Inicializa las configuraciones por defecto del sistema
     */
    private void inicializarConfiguraciones() {
        configuraciones.put("nombreSistema", "AgroSoft");
        configuraciones.put("version", "1.0.0");
        configuraciones.put("maxUsuarios", 1000);
        configuraciones.put("tiempoSesionMinutos", 30);
        configuraciones.put("habilitarNotificaciones", true);
        configuraciones.put("idioma", "es");
        configuraciones.put("formatoFecha", "dd/MM/yyyy");
        configuraciones.put("rutaReportes", "reportes/");
        configuraciones.put("tamanoMaxArchivoMB", 10);
        configuraciones.put("habilitarCache", true);
    }
    
    /**
     * Obtiene una configuración por su clave
     * 
     * @param clave La clave de la configuración
     * @return El valor de la configuración o null si no existe
     */
    public Object obtenerConfiguracion(String clave) {
        return configuraciones.get(clave);
    }
    
    /**
     * Obtiene una configuración con tipo específico
     * 
     * @param clave La clave de la configuración
     * @param tipoClase La clase del tipo esperado
     * @return El valor de la configuración con el tipo especificado
     */
    @SuppressWarnings("unchecked")
    public <T> T obtenerConfiguracion(String clave, Class<T> tipoClase) {
        Object valor = configuraciones.get(clave);
        if (valor != null && tipoClase.isInstance(valor)) {
            return (T) valor;
        }
        return null;
    }
    
    /**
     * Establece o actualiza una configuración
     * 
     * @param clave La clave de la configuración
     * @param valor El valor a establecer
     */
    public void establecerConfiguracion(String clave, Object valor) {
        synchronized (configuraciones) {
            configuraciones.put(clave, valor);
            logger.info("Configuración actualizada: {} = {}", clave, valor);
        }
    }
    
    /**
     * Obtiene todas las configuraciones
     * 
     * @return Un mapa con todas las configuraciones (copia para evitar modificaciones externas)
     */
    public Map<String, Object> obtenerTodasLasConfiguraciones() {
        synchronized (configuraciones) {
            return new HashMap<>(configuraciones);
        }
    }
    
    /**
     * Verifica si existe una configuración
     * 
     * @param clave La clave a verificar
     * @return true si existe, false en caso contrario
     */
    public boolean existeConfiguracion(String clave) {
        return configuraciones.containsKey(clave);
    }
    
    /**
     * Elimina una configuración
     * 
     * @param clave La clave de la configuración a eliminar
     * @return El valor eliminado o null si no existía
     */
    public Object eliminarConfiguracion(String clave) {
        synchronized (configuraciones) {
            Object valor = configuraciones.remove(clave);
            if (valor != null) {
                logger.info("Configuración eliminada: {}", clave);
            }
            return valor;
        }
    }
    
    /**
     * Obtiene el nombre del sistema
     */
    public String getNombreSistema() {
        return obtenerConfiguracion("nombreSistema", String.class);
    }
    
    /**
     * Obtiene la versión del sistema
     */
    public String getVersion() {
        return obtenerConfiguracion("version", String.class);
    }
    
    /**
     * Obtiene la ruta de reportes
     */
    public String getRutaReportes() {
        return obtenerConfiguracion("rutaReportes", String.class);
    }
}

