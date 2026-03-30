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

    @Autowired(required = false)
    private AuditoriaService auditoriaService;

    @Autowired(required = false)
    private NotificacionService notificacionService;

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
        if (cultivo.getActivo() == null) {
            cultivo.setActivo(true);
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
        
        Cultivo guardado = cultivoRepository.save(cultivo);
        if (auditoriaService != null) {
            auditoriaService.registrar("CREAR", "Cultivo", guardado.getId(), guardado.getNombre());
        }
        if (notificacionService != null) {
            String msg = "Nuevo cultivo registrado: " + guardado.getNombre();
            notificacionService.notificarAdministradores(msg, "CULTIVO", "/admin/cultivos");
        }
        return guardado;
    }

    @SuppressWarnings("null")
    public Cultivo actualizarCultivo(Cultivo cultivo) {
        Long id = cultivo.getId();
        if (id == null) {
            throw new IllegalArgumentException("El ID del cultivo es obligatorio para actualizar");
        }
        Cultivo existente = cultivoRepository.findById(id)
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
        
        Cultivo actualizado = cultivoRepository.save(existente);
        if (auditoriaService != null) {
            auditoriaService.registrar("ACTUALIZAR", "Cultivo", actualizado.getId(), actualizado.getNombre());
        }
        return actualizado;
    }

    @SuppressWarnings("null")
    public void eliminarCultivo(Long id) {
        Optional<Cultivo> cultivo = cultivoRepository.findById(id);
        if (auditoriaService != null) {
            String detalle = cultivo.map(Cultivo::getNombre).orElse("Cultivo eliminado");
            auditoriaService.registrar("ELIMINAR", "Cultivo", id, detalle);
        }
        cultivoRepository.deleteById(id);
    }

    @SuppressWarnings("null")
    public Optional<Cultivo> obtenerCultivoPorId(Long id) {
        return cultivoRepository.findById(id);
    }

    public void cambiarEstadoCultivo(Long id, Boolean activo) {
        Cultivo cultivo = obtenerCultivoPorId(id)
            .orElseThrow(() -> new RuntimeException("Cultivo no encontrado"));
        
        cultivo.setActivo(activo);
        cultivo.setEstado(activo ? "Activo" : "Inactivo");
        cultivo.setFechaActualizacion(java.time.LocalDateTime.now());
        Cultivo guardado = cultivoRepository.save(cultivo);
        if (auditoriaService != null) {
            auditoriaService.registrar(activo ? "ACTIVAR" : "DESACTIVAR", "Cultivo", guardado.getId(), "Estado: " + activo);
        }
    }
}
