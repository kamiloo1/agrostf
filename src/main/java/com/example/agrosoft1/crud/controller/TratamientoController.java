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
    public String listar(Model model, @RequestParam(required = false) String success,
                         @RequestParam(required = false) String error) {
        try {
            java.util.List<Tratamiento> tratamientos = tratamientoService.listarTratamientos();
            java.util.List<Ganado> ganado = ganadoService.listarGanado();
            model.addAttribute("tratamientos", tratamientos != null ? tratamientos : new java.util.ArrayList<>());
            model.addAttribute("ganado", ganado != null ? ganado : new java.util.ArrayList<>());
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(TratamientoController.class).error("Error al cargar tratamientos", e);
            model.addAttribute("tratamientos", new java.util.ArrayList<>());
            model.addAttribute("ganado", new java.util.ArrayList<>());
            model.addAttribute("error", "Error al cargar datos: " + e.getMessage());
        }
        if (success != null) {
            if ("actualizado".equals(success)) {
                model.addAttribute("success", "Tratamiento actualizado correctamente");
            } else {
                model.addAttribute("success", "Tratamiento registrado correctamente");
            }
        }
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "dashboard/tratamientos";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam Long idGanado,
                          @RequestParam String tipoTratamiento,
                          @RequestParam String fechaTratamiento,
                          @RequestParam(required = false) String observaciones,
                          @RequestParam(required = false) String veterinarioResponsable,
                          @RequestParam(required = false) Double costo,
                          @RequestParam(required = false) String tipoTratamientoOtro,
                          Model model) {
        try {
            Tratamiento tratamiento = new Tratamiento();
            
            Ganado ganado = ganadoService.obtenerGanadoPorId(idGanado)
                    .orElseThrow(() -> new IllegalArgumentException("Ganado no encontrado"));
            tratamiento.setGanado(ganado);
            
            // Si el tipo es "Otro", usar el valor del campo de texto
            String tipoFinal = "Otro".equals(tipoTratamiento) && tipoTratamientoOtro != null && !tipoTratamientoOtro.trim().isEmpty()
                    ? tipoTratamientoOtro.trim()
                    : tipoTratamiento.trim();
            
            tratamiento.setTipoTratamiento(tipoFinal);
            tratamiento.setFechaTratamiento(LocalDate.parse(fechaTratamiento));
            tratamiento.setObservaciones(observaciones != null ? observaciones.trim() : null);
            tratamiento.setVeterinarioResponsable(veterinarioResponsable != null ? veterinarioResponsable.trim() : null);
            if (costo != null && costo > 0) {
                tratamiento.setCosto(BigDecimal.valueOf(costo));
            }
            
            tratamientoService.guardarTratamiento(tratamiento);
            return "redirect:/vet/tratamientos?success=true";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("tratamientos", tratamientoService.listarTratamientos());
            model.addAttribute("ganado", ganadoService.listarGanado());
            return "dashboard/tratamientos";
        } catch (Exception e) {
            model.addAttribute("error", "Error al guardar el tratamiento: " + e.getMessage());
            model.addAttribute("tratamientos", tratamientoService.listarTratamientos());
            model.addAttribute("ganado", ganadoService.listarGanado());
            return "dashboard/tratamientos";
        }
    }

    @PostMapping("/actualizar")
    public String actualizar(@RequestParam Long idTratamiento,
                            @RequestParam Long idGanado,
                            @RequestParam String tipoTratamiento,
                            @RequestParam String fechaTratamiento,
                            @RequestParam(required = false) String observaciones,
                            @RequestParam(required = false) String veterinarioResponsable,
                            @RequestParam(required = false) Double costo,
                            @RequestParam(required = false) String tipoTratamientoOtro,
                            Model model) {
        try {
            Tratamiento tratamiento = tratamientoService.obtenerTratamientoPorId(idTratamiento)
                    .orElseThrow(() -> new IllegalArgumentException("Tratamiento no encontrado"));
            
            Ganado ganado = ganadoService.obtenerGanadoPorId(idGanado)
                    .orElseThrow(() -> new IllegalArgumentException("Ganado no encontrado"));
            tratamiento.setGanado(ganado);
            
            // Si el tipo es "Otro", usar el valor del campo de texto
            String tipoFinal = "Otro".equals(tipoTratamiento) && tipoTratamientoOtro != null && !tipoTratamientoOtro.trim().isEmpty()
                    ? tipoTratamientoOtro.trim()
                    : tipoTratamiento.trim();
            
            tratamiento.setTipoTratamiento(tipoFinal);
            tratamiento.setFechaTratamiento(LocalDate.parse(fechaTratamiento));
            tratamiento.setObservaciones(observaciones != null ? observaciones.trim() : null);
            tratamiento.setVeterinarioResponsable(veterinarioResponsable != null ? veterinarioResponsable.trim() : null);
            if (costo != null && costo > 0) {
                tratamiento.setCosto(BigDecimal.valueOf(costo));
            } else {
                tratamiento.setCosto(null);
            }
            
            tratamientoService.actualizarTratamiento(tratamiento);
            return "redirect:/vet/tratamientos?success=actualizado";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("tratamientos", tratamientoService.listarTratamientos());
            model.addAttribute("ganado", ganadoService.listarGanado());
            return "dashboard/tratamientos";
        } catch (Exception e) {
            model.addAttribute("error", "Error al actualizar el tratamiento: " + e.getMessage());
            model.addAttribute("tratamientos", tratamientoService.listarTratamientos());
            model.addAttribute("ganado", ganadoService.listarGanado());
            return "dashboard/tratamientos";
        }
    }

    @DeleteMapping("/eliminar/{id}")
    @ResponseBody
    public String eliminar(@PathVariable Long id) {
        tratamientoService.eliminarTratamiento(id);
        return "{\"status\":\"ok\"}";
    }
}
