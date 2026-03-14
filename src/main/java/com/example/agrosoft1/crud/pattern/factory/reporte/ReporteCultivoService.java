package com.example.agrosoft1.crud.pattern.factory.reporte;

import com.example.agrosoft1.crud.repository.CultivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Servicio de reporte específico para cultivos
 * Implementa ReporteService para reportes relacionados con cultivos
 */
@Service
public class ReporteCultivoService implements ReporteService {
    
    @Autowired
    private CultivoRepository cultivoRepository;
    
    @Override
    public Map<String, Object> generarReporte() {
        Map<String, Object> reporte = new HashMap<>();
        
        long totalCultivos = cultivoRepository.count();
        long cultivosActivos = cultivoRepository.findAll().stream()
                .filter(c -> c.getEstado() != null && c.getEstado().equalsIgnoreCase("Activo"))
                .count();
        
        reporte.put("totalCultivos", totalCultivos);
        reporte.put("cultivosActivos", cultivosActivos);
        reporte.put("cultivosInactivos", totalCultivos - cultivosActivos);
        reporte.put("tipo", "CULTIVO");
        reporte.put("fechaGeneracion", java.time.LocalDateTime.now());
        
        return reporte;
    }
    
    @Override
    public String getNombreReporte() {
        return "Reporte de Cultivos";
    }
    
    @Override
    public String getDescripcion() {
        return "Reporte estadístico de cultivos del sistema";
    }
}

