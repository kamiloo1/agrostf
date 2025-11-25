package com.example.agrosoft1.crud.service;

import com.example.agrosoft1.crud.entity.Cultivo;
import com.example.agrosoft1.crud.repository.CultivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CultivoService {

    @Autowired
    private CultivoRepository cultivoRepository;

    public List<Cultivo> listarCultivos() {
        return cultivoRepository.findAll();
    }

    public Cultivo guardarCultivo(Cultivo cultivo) {
        if (cultivo.getFechaCreacion() == null) {
            cultivo.setFechaCreacion(java.time.LocalDateTime.now());
        }
        if (cultivo.getEstado() == null || cultivo.getEstado().isEmpty()) {
            cultivo.setEstado("Activo");
        }
        cultivo.setFechaActualizacion(java.time.LocalDateTime.now());
        
        // Construir descripción desde los campos adicionales si existen
        if (cultivo.getDescripcion() == null || cultivo.getDescripcion().isEmpty()) {
            StringBuilder desc = new StringBuilder();
            if (cultivo.getTipo() != null && !cultivo.getTipo().isEmpty()) {
                desc.append("Tipo: ").append(cultivo.getTipo());
            }
            if (cultivo.getArea() != null && !cultivo.getArea().isEmpty()) {
                if (desc.length() > 0) desc.append("\n");
                desc.append("Área: ").append(cultivo.getArea());
            }
            if (cultivo.getObservaciones() != null && !cultivo.getObservaciones().isEmpty()) {
                if (desc.length() > 0) desc.append("\n");
                desc.append(cultivo.getObservaciones());
            }
            if (desc.length() > 0) {
                cultivo.setDescripcion(desc.toString());
            }
        }
        
        return cultivoRepository.save(cultivo);
    }

    public Cultivo actualizarCultivo(Cultivo cultivo) {
        Cultivo existente = cultivoRepository.findById(cultivo.getId())
                .orElseThrow(() -> new RuntimeException("Cultivo no encontrado"));
        
        existente.setNombre(cultivo.getNombre());
        
        // Actualizar campos adicionales y construir descripción
        existente.setTipo(cultivo.getTipo());
        existente.setArea(cultivo.getArea());
        existente.setEstado(cultivo.getEstado());
        existente.setFechaSiembra(cultivo.getFechaSiembra());
        existente.setFechaCosecha(cultivo.getFechaCosecha());
        existente.setObservaciones(cultivo.getObservaciones());
        existente.setFechaActualizacion(java.time.LocalDateTime.now());
        
        // Construir descripción actualizada
        StringBuilder desc = new StringBuilder();
        if (cultivo.getTipo() != null && !cultivo.getTipo().isEmpty()) {
            desc.append("Tipo: ").append(cultivo.getTipo());
        }
        if (cultivo.getArea() != null && !cultivo.getArea().isEmpty()) {
            if (desc.length() > 0) desc.append("\n");
            desc.append("Área: ").append(cultivo.getArea());
        }
        if (cultivo.getObservaciones() != null && !cultivo.getObservaciones().isEmpty()) {
            if (desc.length() > 0) desc.append("\n");
            desc.append(cultivo.getObservaciones());
        }
        if (desc.length() > 0) {
            existente.setDescripcion(desc.toString());
        }
        
        return cultivoRepository.save(existente);
    }

    public void eliminarCultivo(Long id) {
        cultivoRepository.deleteById(id);
    }

    public Optional<Cultivo> obtenerCultivoPorId(Long id) {
        return cultivoRepository.findById(id);
    }
}
