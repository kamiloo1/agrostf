package com.example.agrosoft1.crud.service;

import com.example.agrosoft1.crud.entity.Actividad;
import com.example.agrosoft1.crud.entity.Cultivo;
import com.example.agrosoft1.crud.repository.ActividadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de actividades agrícolas
 */
@Service
public class ActividadService {

    private static final Logger logger = LoggerFactory.getLogger(ActividadService.class);
    
    private final ActividadRepository actividadRepository;
    private final CultivoService cultivoService;

    public ActividadService(ActividadRepository actividadRepository, CultivoService cultivoService) {
        this.actividadRepository = actividadRepository;
        this.cultivoService = cultivoService;
    }

    /**
     * Lista todas las actividades
     */
    public List<Actividad> listarActividades() {
        return actividadRepository.findAll();
    }

    /**
     * Guarda una nueva actividad con validaciones
     */
    @Transactional
    public Actividad guardarActividad(Actividad actividad) {
        // Validaciones
        if (actividad.getTipoActividad() == null || actividad.getTipoActividad().trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de actividad es obligatorio");
        }
        if (actividad.getFechaActividad() == null) {
            throw new IllegalArgumentException("La fecha de actividad es obligatoria");
        }
        
        // Validar que el cultivo existe si está presente
        if (actividad.getCultivo() != null && actividad.getCultivo().getId() != null) {
            Optional<Cultivo> cultivo = cultivoService.obtenerCultivoPorId(actividad.getCultivo().getId());
            if (cultivo.isEmpty()) {
                throw new IllegalArgumentException("El cultivo especificado no existe");
            }
            actividad.setCultivo(cultivo.get());
        }
        
        // Valores por defecto
        if (actividad.getFechaCreacion() == null) {
            actividad.setFechaCreacion(LocalDateTime.now());
        }
        if (actividad.getEstado() == null || actividad.getEstado().trim().isEmpty()) {
            actividad.setEstado("Pendiente");
        }
        
        logger.info("Guardando actividad: {} - {}", actividad.getTipoActividad(), actividad.getFechaActividad());
        return actividadRepository.save(actividad);
    }

    /**
     * Actualiza una actividad existente
     */
    @Transactional
    public Actividad actualizarActividad(Actividad actividad) {
        if (actividad.getIdActividad() == null) {
            throw new IllegalArgumentException("El ID de la actividad es obligatorio para actualizar");
        }
        
        Actividad existente = actividadRepository.findById(actividad.getIdActividad())
                .orElseThrow(() -> new RuntimeException("Actividad no encontrada con ID: " + actividad.getIdActividad()));
        
        // Validar cultivo si se está actualizando
        if (actividad.getCultivo() != null && actividad.getCultivo().getId() != null) {
            Optional<Cultivo> cultivo = cultivoService.obtenerCultivoPorId(actividad.getCultivo().getId());
            if (cultivo.isEmpty()) {
                throw new IllegalArgumentException("El cultivo especificado no existe");
            }
            existente.setCultivo(cultivo.get());
        }
        
        // Actualizar campos
        if (actividad.getTipoActividad() != null && !actividad.getTipoActividad().trim().isEmpty()) {
            existente.setTipoActividad(actividad.getTipoActividad());
        }
        if (actividad.getDescripcion() != null) {
            existente.setDescripcion(actividad.getDescripcion());
        }
        if (actividad.getFechaActividad() != null) {
            existente.setFechaActividad(actividad.getFechaActividad());
        }
        if (actividad.getTrabajadorResponsable() != null) {
            existente.setTrabajadorResponsable(actividad.getTrabajadorResponsable());
        }
        if (actividad.getEstado() != null && !actividad.getEstado().trim().isEmpty()) {
            existente.setEstado(actividad.getEstado());
        }
        
        logger.info("Actualizando actividad ID: {}", actividad.getIdActividad());
        return actividadRepository.save(existente);
    }

    /**
     * Elimina una actividad por ID
     */
    @Transactional
    public void eliminarActividad(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID de la actividad es obligatorio");
        }
        if (!actividadRepository.existsById(id)) {
            throw new RuntimeException("Actividad no encontrada con ID: " + id);
        }
        logger.info("Eliminando actividad ID: {}", id);
        actividadRepository.deleteById(id);
    }

    /**
     * Obtiene una actividad por ID
     */
    public Optional<Actividad> obtenerActividadPorId(Long id) {
        return actividadRepository.findById(id);
    }

    /**
     * Busca actividades por cultivo
     */
    public List<Actividad> buscarPorCultivo(Long idCultivo) {
        if (idCultivo == null) {
            throw new IllegalArgumentException("El ID del cultivo es obligatorio");
        }
        return actividadRepository.findByCultivo_Id(idCultivo);
    }

    /**
     * Busca actividades por tipo
     */
    public List<Actividad> buscarPorTipo(String tipoActividad) {
        if (tipoActividad == null || tipoActividad.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de actividad es obligatorio");
        }
        return actividadRepository.findByTipoActividad(tipoActividad);
    }

    /**
     * Busca actividades por estado
     */
    public List<Actividad> buscarPorEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado es obligatorio");
        }
        return actividadRepository.findByEstado(estado);
    }

    /**
     * Busca actividades por trabajador responsable
     */
    public List<Actividad> buscarPorTrabajador(String trabajador) {
        if (trabajador == null || trabajador.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del trabajador es obligatorio");
        }
        return actividadRepository.findByTrabajadorResponsable(trabajador);
    }

    /**
     * Busca actividades por fecha
     */
    public List<Actividad> buscarPorFecha(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }
        return actividadRepository.findByFechaActividad(fecha);
    }

    /**
     * Cuenta actividades pendientes
     */
    public long contarActividadesPendientes() {
        return actividadRepository.countByEstado("Pendiente");
    }

    /**
     * Cuenta actividades completadas
     */
    public long contarActividadesCompletadas() {
        return actividadRepository.countByEstado("Completada");
    }

    /**
     * Cuenta actividades por cultivo
     */
    public long contarActividadesPorCultivo(Long idCultivo) {
        if (idCultivo == null) {
            throw new IllegalArgumentException("El ID del cultivo es obligatorio");
        }
        return actividadRepository.countByCultivo_Id(idCultivo);
    }

    /**
     * Verifica si existe una actividad con el ID dado
     */
    public boolean existeActividad(Long id) {
        return id != null && actividadRepository.existsById(id);
    }
}
