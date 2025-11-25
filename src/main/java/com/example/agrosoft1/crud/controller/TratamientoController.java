package com.example.agrosoft1.crud.controller;

import com.example.agrosoft1.crud.entity.Ganado;
import com.example.agrosoft1.crud.entity.Tratamiento;
import com.example.agrosoft1.crud.service.GanadoService;
import com.example.agrosoft1.crud.service.TratamientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/vet/tratamientos")
public class TratamientoController {

    @Autowired
    private TratamientoService tratamientoService;

    @Autowired
    private GanadoService ganadoService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("tratamientos", tratamientoService.listarTratamientos());
        model.addAttribute("ganado", ganadoService.listarGanado());
        return "dashboard/tratamientos";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam Long idGanado,
                          @RequestParam String tipoTratamiento,
                          @RequestParam String fechaTratamiento,
                          @RequestParam(required = false) String observaciones,
                          @RequestParam(required = false) String veterinarioResponsable,
                          @RequestParam(required = false) Double costo) {
        Tratamiento tratamiento = new Tratamiento();
        
        Ganado ganado = ganadoService.obtenerGanadoPorId(idGanado)
                .orElseThrow(() -> new RuntimeException("Ganado no encontrado"));
        tratamiento.setGanado(ganado);
        
        tratamiento.setTipoTratamiento(tipoTratamiento);
        tratamiento.setFechaTratamiento(LocalDate.parse(fechaTratamiento));
        tratamiento.setObservaciones(observaciones);
        tratamiento.setVeterinarioResponsable(veterinarioResponsable);
        if (costo != null) {
            tratamiento.setCosto(BigDecimal.valueOf(costo));
        }
        
        tratamientoService.guardarTratamiento(tratamiento);
        return "redirect:/vet/tratamientos";
    }

    @PostMapping("/actualizar")
    public String actualizar(@RequestParam Long idTratamiento,
                            @RequestParam Long idGanado,
                            @RequestParam String tipoTratamiento,
                            @RequestParam String fechaTratamiento,
                            @RequestParam(required = false) String observaciones,
                            @RequestParam(required = false) String veterinarioResponsable,
                            @RequestParam(required = false) Double costo) {
        Tratamiento tratamiento = tratamientoService.obtenerTratamientoPorId(idTratamiento)
                .orElseThrow(() -> new RuntimeException("Tratamiento no encontrado"));
        
        Ganado ganado = ganadoService.obtenerGanadoPorId(idGanado)
                .orElseThrow(() -> new RuntimeException("Ganado no encontrado"));
        tratamiento.setGanado(ganado);
        
        tratamiento.setTipoTratamiento(tipoTratamiento);
        tratamiento.setFechaTratamiento(LocalDate.parse(fechaTratamiento));
        tratamiento.setObservaciones(observaciones);
        tratamiento.setVeterinarioResponsable(veterinarioResponsable);
        if (costo != null) {
            tratamiento.setCosto(BigDecimal.valueOf(costo));
        }
        
        tratamientoService.actualizarTratamiento(tratamiento);
        return "redirect:/vet/tratamientos";
    }

    @DeleteMapping("/eliminar/{id}")
    @ResponseBody
    public String eliminar(@PathVariable Long id) {
        tratamientoService.eliminarTratamiento(id);
        return "{\"status\":\"ok\"}";
    }
}
