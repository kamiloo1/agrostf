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
        
        ClimaDTO clima = climaService.obtenerClimaActual(ciudad, pais);
        model.addAttribute("clima", clima);
        model.addAttribute("ciudad", ciudad);
        model.addAttribute("pais", pais);
        
        return "dashboard/clima";
    }
    
    /**
     * Endpoint REST para obtener clima (API)
     */
    @GetMapping("/api")
    @ResponseBody
    public ClimaDTO obtenerClimaAPI(
            @RequestParam(required = false, defaultValue = "Bogotá") String ciudad,
            @RequestParam(required = false, defaultValue = "CO") String pais) {
        return climaService.obtenerClimaActual(ciudad, pais);
    }
}

