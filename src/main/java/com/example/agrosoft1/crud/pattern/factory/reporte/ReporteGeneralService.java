package com.example.agrosoft1.crud.pattern.factory.reporte;

import com.example.agrosoft1.crud.repository.CultivoRepository;
import com.example.agrosoft1.crud.repository.GanadoRepository;
import com.example.agrosoft1.crud.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Servicio de reporte general del sistema
 * Implementa ReporteService para reportes generales que incluyen múltiples entidades
 */
@Service
public class ReporteGeneralService implements ReporteService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private CultivoRepository cultivoRepository;
    
    @Autowired
    private GanadoRepository ganadoRepository;
    
    @Override
    public Map<String, Object> generarReporte() {
        Map<String, Object> reporte = new HashMap<>();
        
        long totalUsuarios = usuarioRepository.count();
        long totalCultivos = cultivoRepository.count();
        long totalGanado = ganadoRepository.count();
        
        reporte.put("totalUsuarios", totalUsuarios);
        reporte.put("totalCultivos", totalCultivos);
        reporte.put("totalGanado", totalGanado);
        reporte.put("totalRegistros", totalUsuarios + totalCultivos + totalGanado);
        reporte.put("tipo", "GENERAL");
        reporte.put("fechaGeneracion", java.time.LocalDateTime.now());
        
        return reporte;
    }
    
    @Override
    public String getNombreReporte() {
        return "Reporte General del Sistema";
    }
    
    @Override
    public String getDescripcion() {
        return "Reporte estadístico general que incluye usuarios, cultivos y ganado";
    }
}

