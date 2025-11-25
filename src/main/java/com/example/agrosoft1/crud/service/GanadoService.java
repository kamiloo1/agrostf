package com.example.agrosoft1.crud.service;

import com.example.agrosoft1.crud.entity.Ganado;
import com.example.agrosoft1.crud.repository.GanadoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de ganado
 */
@Service
public class GanadoService {

    private static final Logger logger = LoggerFactory.getLogger(GanadoService.class);
    
    private final GanadoRepository ganadoRepository;
    private final TratamientoService tratamientoService;

    public GanadoService(GanadoRepository ganadoRepository, TratamientoService tratamientoService) {
        this.ganadoRepository = ganadoRepository;
        this.tratamientoService = tratamientoService;
    }

    /**
     * Lista todo el ganado
     */
    public List<Ganado> listarGanado() {
        return ganadoRepository.findAll();
    }

    /**
     * Lista solo el ganado activo
     */
    public List<Ganado> listarGanadoActivo() {
        return ganadoRepository.findByActivoTrue();
    }

    /**
     * Guarda un nuevo registro de ganado con validaciones
     */
    @Transactional
    public Ganado guardarGanado(Ganado ganado) {
        // Validar campos obligatorios
        if (ganado.getTipo() == null || ganado.getTipo().trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de ganado es obligatorio");
        }
        
        // Valores por defecto
        if (ganado.getFechaCreacion() == null) {
            ganado.setFechaCreacion(LocalDateTime.now());
        }
        if (ganado.getActivo() == null) {
            ganado.setActivo(true);
        }
        
        // Validar que la fecha de nacimiento no sea futura
        if (ganado.getFechaNacimiento() != null && ganado.getFechaNacimiento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura");
        }
        
        // Calcular edad automáticamente si hay fecha de nacimiento pero no edad
        if (ganado.getFechaNacimiento() != null && ganado.getEdad() == null) {
            ganado.setEdad(calcularEdad(ganado.getFechaNacimiento()));
        }
        
        logger.info("Guardando ganado: {} - {}", ganado.getTipo(), ganado.getRaza());
        return ganadoRepository.save(ganado);
    }

    /**
     * Actualiza un registro de ganado existente
     */
    @Transactional
    public Ganado actualizarGanado(Ganado ganado) {
        if (ganado.getIdGanado() == null) {
            throw new IllegalArgumentException("El ID del ganado es obligatorio para actualizar");
        }
        
        Ganado existente = ganadoRepository.findById(ganado.getIdGanado())
                .orElseThrow(() -> new RuntimeException("Ganado no encontrado con ID: " + ganado.getIdGanado()));
        
        // Actualizar campos
        if (ganado.getTipo() != null && !ganado.getTipo().trim().isEmpty()) {
            existente.setTipo(ganado.getTipo());
        }
        if (ganado.getRaza() != null) {
            existente.setRaza(ganado.getRaza());
        }
        if (ganado.getEdad() != null) {
            existente.setEdad(ganado.getEdad());
        }
        if (ganado.getPeso() != null) {
            existente.setPeso(ganado.getPeso());
        }
        if (ganado.getEstadoSalud() != null) {
            existente.setEstadoSalud(ganado.getEstadoSalud());
        }
        if (ganado.getFechaNacimiento() != null) {
            // Validar que la fecha de nacimiento no sea futura
            if (ganado.getFechaNacimiento().isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura");
            }
            existente.setFechaNacimiento(ganado.getFechaNacimiento());
            // Recalcular edad si se actualiza la fecha de nacimiento
            if (ganado.getEdad() == null) {
                existente.setEdad(calcularEdad(ganado.getFechaNacimiento()));
            }
        }
        if (ganado.getActivo() != null) {
            existente.setActivo(ganado.getActivo());
        }
        
        logger.info("Actualizando ganado ID: {}", ganado.getIdGanado());
        return ganadoRepository.save(existente);
    }

    /**
     * Cambia el estado activo/inactivo de un ganado
     */
    @Transactional
    public Ganado cambiarEstadoGanado(Long id, boolean activo) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del ganado es obligatorio");
        }
        
        Ganado existente = ganadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ganado no encontrado con ID: " + id));
        
        existente.setActivo(activo);
        logger.info("Cambiando estado del ganado ID: {} a {}", id, activo ? "activo" : "inactivo");
        return ganadoRepository.save(existente);
    }

    /**
     * Obtiene un ganado por ID
     */
    public Optional<Ganado> obtenerGanadoPorId(Long id) {
        return ganadoRepository.findById(id);
    }

    /**
     * Elimina un ganado con validación de tratamientos activos
     */
    @Transactional
    public void eliminarGanado(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del ganado es obligatorio");
        }
        
        if (!ganadoRepository.existsById(id)) {
            throw new RuntimeException("Ganado no encontrado con ID: " + id);
        }
        
        // Restricción: No eliminar un paciente (ganado) con tratamientos activos
        long tratamientosCount = tratamientoService.contarTratamientosPorGanado(id);
        if (tratamientosCount > 0) {
            throw new IllegalStateException(
                "No se puede eliminar un paciente (ganado) con tratamientos activos. " +
                "Tiene " + tratamientosCount + " tratamiento(s) asociado(s)."
            );
        }
        
        logger.info("Eliminando ganado ID: {}", id);
        ganadoRepository.deleteById(id);
    }

    /**
     * Busca ganado por tipo
     */
    public List<Ganado> buscarPorTipo(String tipo) {
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de ganado es obligatorio");
        }
        return ganadoRepository.findByTipo(tipo);
    }

    /**
     * Busca ganado por estado de salud
     */
    public List<Ganado> buscarPorEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado de salud es obligatorio");
        }
        return ganadoRepository.findByEstadoSalud(estado);
    }

    /**
     * Busca ganado por raza
     */
    public List<Ganado> buscarPorRaza(String raza) {
        if (raza == null || raza.trim().isEmpty()) {
            throw new IllegalArgumentException("La raza es obligatoria");
        }
        return ganadoRepository.findByRaza(raza);
    }

    /**
     * Cuenta el total de ganado activo
     */
    public long contarGanado() {
        return ganadoRepository.countByActivoTrue();
    }

    /**
     * Cuenta el ganado saludable y activo
     */
    public long contarGanadoSaludable() {
        return ganadoRepository.countByEstadoSaludAndActivoTrue("Saludable");
    }

    /**
     * Cuenta ganado por tipo
     */
    public long contarGanadoPorTipo(String tipo) {
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de ganado es obligatorio");
        }
        return ganadoRepository.countByTipo(tipo);
    }

    /**
     * Cuenta ganado por estado de salud
     */
    public long contarGanadoPorEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado de salud es obligatorio");
        }
        return ganadoRepository.countByEstadoSalud(estado);
    }

    /**
     * Verifica si existe un ganado con el ID dado
     */
    public boolean existeGanado(Long id) {
        return id != null && ganadoRepository.existsById(id);
    }

    /**
     * Calcula la edad en años basándose en la fecha de nacimiento
     */
    private int calcularEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) {
            return 0;
        }
        LocalDate hoy = LocalDate.now();
        int edad = hoy.getYear() - fechaNacimiento.getYear();
        if (hoy.getDayOfYear() < fechaNacimiento.getDayOfYear()) {
            edad--;
        }
        return Math.max(0, edad);
    }
}
