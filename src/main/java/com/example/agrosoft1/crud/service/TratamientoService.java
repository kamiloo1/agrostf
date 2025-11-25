package com.example.agrosoft1.crud.service;

import com.example.agrosoft1.crud.entity.Tratamiento;
import com.example.agrosoft1.crud.repository.TratamientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TratamientoService {

    @Autowired
    private TratamientoRepository tratamientoRepository;

    @Autowired
    private GanadoService ganadoService;

    public List<Tratamiento> listarTratamientos() {
        return tratamientoRepository.findAll();
    }

    public Tratamiento guardarTratamiento(Tratamiento tratamiento) {
        // Validación: No crear tratamientos sin paciente (ganado)
        if (tratamiento.getGanado() == null || tratamiento.getGanado().getIdGanado() == null) {
            throw new IllegalArgumentException("No se puede crear un tratamiento sin un paciente (ganado) asociado");
        }
        
        // Validar que el ganado existe
        if (!ganadoService.obtenerGanadoPorId(tratamiento.getGanado().getIdGanado()).isPresent()) {
            throw new IllegalArgumentException("El paciente (ganado) especificado no existe");
        }
        
        // Validar campos obligatorios
        if (tratamiento.getTipoTratamiento() == null || tratamiento.getTipoTratamiento().trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de tratamiento es obligatorio");
        }
        
        if (tratamiento.getFechaTratamiento() == null) {
            throw new IllegalArgumentException("La fecha de tratamiento es obligatoria");
        }
        
        if (tratamiento.getFechaCreacion() == null) {
            tratamiento.setFechaCreacion(java.time.LocalDateTime.now());
        }
        return tratamientoRepository.save(tratamiento);
    }

    public Tratamiento actualizarTratamiento(Tratamiento tratamiento) {
        Tratamiento existente = tratamientoRepository.findById(tratamiento.getIdTratamiento())
                .orElseThrow(() -> new RuntimeException("Tratamiento no encontrado"));
        
        existente.setGanado(tratamiento.getGanado());
        existente.setTipoTratamiento(tratamiento.getTipoTratamiento());
        existente.setFechaTratamiento(tratamiento.getFechaTratamiento());
        existente.setObservaciones(tratamiento.getObservaciones());
        existente.setVeterinarioResponsable(tratamiento.getVeterinarioResponsable());
        existente.setCosto(tratamiento.getCosto());
        
        return tratamientoRepository.save(existente);
    }

    public void eliminarTratamiento(Long id) {
        tratamientoRepository.deleteById(id);
    }

    public Optional<Tratamiento> obtenerTratamientoPorId(Long id) {
        return tratamientoRepository.findById(id);
    }

    public List<Tratamiento> buscarPorGanado(Long idGanado) {
        return tratamientoRepository.findByGanadoIdGanado(idGanado);
    }

    public List<Tratamiento> buscarPorTipo(String tipo) {
        return tratamientoRepository.findByTipoTratamiento(tipo);
    }

    public List<Tratamiento> buscarPorVeterinario(String veterinario) {
        return tratamientoRepository.findByVeterinarioResponsable(veterinario);
    }

    public List<Tratamiento> buscarPorFecha(LocalDate fecha) {
        return tratamientoRepository.findByFechaTratamiento(fecha);
    }

    public long contarTratamientos() {
        return tratamientoRepository.count();
    }
    
    // Contar tratamientos por ganado (para validación de eliminación)
    public long contarTratamientosPorGanado(Long idGanado) {
        return tratamientoRepository.countByGanadoIdGanado(idGanado);
    }
}
