package com.example.agrosoft1.crud.pattern.factory.reporte;

import com.example.agrosoft1.crud.repository.GanadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Servicio de reporte específico para ganado
 * Implementa ReporteService para reportes relacionados con ganado
 */
@Service
public class ReporteGanadoService implements ReporteService {
    
    @Autowired
    private GanadoRepository ganadoRepository;
    
    @Override
    public Map<String, Object> generarReporte() {
        Map<String, Object> reporte = new HashMap<>();
        
        long totalGanado = ganadoRepository.count();
        long ganadoActivo = ganadoRepository.findByActivoTrue().size();
        
        reporte.put("totalGanado", totalGanado);
        reporte.put("ganadoActivo", ganadoActivo);
        reporte.put("ganadoInactivo", totalGanado - ganadoActivo);
        reporte.put("tipo", "GANADO");
        reporte.put("fechaGeneracion", java.time.LocalDateTime.now());
        
        return reporte;
    }
    
    @Override
    public String getNombreReporte() {
        return "Reporte de Ganado";
    }
    
    @Override
    public String getDescripcion() {
        return "Reporte estadístico de ganado del sistema";
    }
}

