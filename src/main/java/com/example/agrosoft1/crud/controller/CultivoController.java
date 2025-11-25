package com.example.agrosoft1.crud.controller;

import com.example.agrosoft1.crud.entity.Cultivo;
import com.example.agrosoft1.crud.service.CultivoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/cultivos")
public class CultivoController {

    private final CultivoService cultivoService;

    public CultivoController(CultivoService cultivoService) {
        this.cultivoService = cultivoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("cultivos", cultivoService.listarCultivos());
        model.addAttribute("usuario", "Administrador");
        return "dashboard/cultivos";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam String nombre,
            @RequestParam String tipo,
            @RequestParam String area,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String fechaSiembra,
            @RequestParam(required = false) String fechaCosecha,
            @RequestParam(required = false) String observaciones) {
        Cultivo cultivo = new Cultivo(null, nombre, tipo, area);
        cultivo.setEstado(estado != null ? estado : "Activo");
        if (fechaSiembra != null && !fechaSiembra.isEmpty()) {
            cultivo.setFechaSiembra(java.time.LocalDate.parse(fechaSiembra));
        }
        if (fechaCosecha != null && !fechaCosecha.isEmpty()) {
            cultivo.setFechaCosecha(java.time.LocalDate.parse(fechaCosecha));
        }
        cultivo.setObservaciones(observaciones);
        cultivoService.guardarCultivo(cultivo);
        return "redirect:/admin/cultivos";
    }

    @PostMapping("/actualizar")
    public String actualizar(@RequestParam Long id,
            @RequestParam String nombre,
            @RequestParam String tipo,
            @RequestParam String area,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String fechaSiembra,
            @RequestParam(required = false) String fechaCosecha,
            @RequestParam(required = false) String observaciones) {
        Cultivo cultivo = cultivoService.obtenerCultivoPorId(id)
                .orElseThrow(() -> new RuntimeException("Cultivo no encontrado"));
        
        cultivo.setId(id); // Asegurar que el ID esté establecido
        cultivo.setNombre(nombre);
        cultivo.setTipo(tipo);
        cultivo.setArea(area);
        if (estado != null) {
            cultivo.setEstado(estado);
        }
        if (fechaSiembra != null && !fechaSiembra.isEmpty()) {
            cultivo.setFechaSiembra(java.time.LocalDate.parse(fechaSiembra));
        }
        if (fechaCosecha != null && !fechaCosecha.isEmpty()) {
            cultivo.setFechaCosecha(java.time.LocalDate.parse(fechaCosecha));
        }
        cultivo.setObservaciones(observaciones);
        
        cultivoService.actualizarCultivo(cultivo);
        return "redirect:/admin/cultivos";
    }

    @DeleteMapping("/eliminar/{id}")
    @ResponseBody
    public String eliminar(@PathVariable Long id) {
        cultivoService.eliminarCultivo(id);
        return "{\"status\":\"ok\"}";
    }
}
