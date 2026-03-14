package com.example.agrosoft1.crud.controller;

import com.example.agrosoft1.crud.dto.ClimaDTO;
import com.example.agrosoft1.crud.service.ClimaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controlador para mostrar información del clima obtenida de API externa.
 * 
 * Consume el servicio de clima y muestra la información en una vista.
 */
@Controller
@RequestMapping("/clima")
public class ClimaController {
    
    @Autowired
    private ClimaService climaService;
    
    /**
     * Muestra la vista con el clima actual
     */
    @GetMapping
    public String mostrarClima(
            @RequestParam(required = false, defaultValue = "Bogotá") String ciudad,
            @RequestParam(required = false, defaultValue = "CO") String pais,
            Model model) {
        
        // Siempre usar Colombia como país por defecto
        ClimaDTO clima = climaService.obtenerClimaActual(ciudad, "CO");
        model.addAttribute("clima", clima);
        model.addAttribute("ciudad", ciudad);
        model.addAttribute("pais", "CO");
        
        return "dashboard/clima";
    }
    
    /**
     * Endpoint REST para obtener clima (API) - Para uso con AJAX
     */
    @GetMapping("/api")
    @ResponseBody
    public ClimaDTO obtenerClimaAPI(
            @RequestParam(required = false, defaultValue = "Bogotá") String ciudad,
            @RequestParam(required = false, defaultValue = "CO") String pais,
            @RequestParam(required = false) String _t) { // Timestamp para evitar cache
        // El parámetro _t se ignora pero ayuda a evitar cache del navegador
        // Siempre usar Colombia como país
        return climaService.obtenerClimaActual(ciudad, "CO", true); // Forzar actualización
    }
    
    /**
     * Endpoint REST para búsqueda rápida (solo ciudad)
     */
    @GetMapping("/buscar")
    @ResponseBody
    public ClimaDTO buscarClima(@RequestParam String ciudad) {
        // Buscar ciudad en Colombia
        return climaService.obtenerClimaActual(ciudad, "CO");
    }
    
    /**
     * Endpoint REST para obtener estadísticas del cache del Web Service
     */
    @GetMapping("/estadisticas")
    @ResponseBody
    public java.util.Map<String, Object> obtenerEstadisticas() {
        return climaService.obtenerEstadisticasCache();
    }
    
    /**
     * Endpoint REST para limpiar el cache del Web Service
     */
    @GetMapping("/limpiar-cache")
    @ResponseBody
    public java.util.Map<String, String> limpiarCache() {
        climaService.limpiarCacheExpirado();
        return java.util.Map.of(
            "mensaje", "Cache limpiado exitosamente",
            "estado", "ok"
        );
    }
}

