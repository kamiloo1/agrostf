package com.example.agrosoft1.crud.controller;

import com.example.agrosoft1.crud.entity.Ganado;
import com.example.agrosoft1.crud.service.GanadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin/ganado")
public class GanadoController {

    @Autowired
    private GanadoService ganadoService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("ganado", ganadoService.listarGanado());
        return "dashboard/ganado";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam String tipo,
                         @RequestParam(required = false) String raza,
                         @RequestParam(required = false) Integer edad,
                         @RequestParam(required = false) Double peso,
                         @RequestParam(required = false) String estadoSalud,
                         @RequestParam(required = false) String fechaNacimiento) {
        Ganado ganado = new Ganado();
        ganado.setTipo(tipo);
        ganado.setRaza(raza);
        ganado.setEdad(edad);
        ganado.setPeso(peso);
        ganado.setEstadoSalud(estadoSalud != null ? estadoSalud : "Saludable");
        if (fechaNacimiento != null && !fechaNacimiento.isEmpty()) {
            ganado.setFechaNacimiento(LocalDate.parse(fechaNacimiento));
        }
        ganadoService.guardarGanado(ganado);
        return "redirect:/admin/ganado";
    }

    @PostMapping("/actualizar")
    public String actualizar(@RequestParam Long idGanado,
                            @RequestParam String tipo,
                            @RequestParam(required = false) String raza,
                            @RequestParam(required = false) Integer edad,
                            @RequestParam(required = false) Double peso,
                            @RequestParam(required = false) String estadoSalud,
                            @RequestParam(required = false) String fechaNacimiento,
                            @RequestParam(required = false, defaultValue = "true") Boolean activo) {
        Ganado ganado = ganadoService.obtenerGanadoPorId(idGanado)
                .orElseThrow(() -> new RuntimeException("Ganado no encontrado"));
        
        ganado.setTipo(tipo);
        ganado.setRaza(raza);
        ganado.setEdad(edad);
        ganado.setPeso(peso);
        ganado.setEstadoSalud(estadoSalud);
        if (fechaNacimiento != null && !fechaNacimiento.isEmpty()) {
            ganado.setFechaNacimiento(LocalDate.parse(fechaNacimiento));
        }
        ganado.setActivo(activo != null ? activo : Boolean.TRUE);
        
        ganadoService.actualizarGanado(ganado);
        return "redirect:/admin/ganado";
    }

    @PostMapping("/estado/{id}")
    @ResponseBody
    public String cambiarEstado(@PathVariable Long id, @RequestParam boolean activo) {
        ganadoService.cambiarEstadoGanado(id, activo);
        return "{\"status\":\"ok\"}";
    }
    
    @DeleteMapping("/eliminar/{id}")
    @ResponseBody
    public String eliminar(@PathVariable Long id) {
        try {
            ganadoService.eliminarGanado(id);
            return "{\"status\":\"ok\",\"message\":\"Paciente eliminado correctamente\"}";
        } catch (IllegalStateException e) {
            return "{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}";
        } catch (Exception e) {
            return "{\"status\":\"error\",\"message\":\"Error al eliminar paciente: " + e.getMessage() + "\"}";
        }
    }
}
