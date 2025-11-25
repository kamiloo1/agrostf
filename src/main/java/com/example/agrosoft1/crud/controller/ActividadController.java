package com.example.agrosoft1.crud.controller;

import com.example.agrosoft1.crud.entity.Actividad;
import com.example.agrosoft1.crud.entity.Cultivo;
import com.example.agrosoft1.crud.service.ActividadService;
import com.example.agrosoft1.crud.service.CultivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/trabajador/actividades")
public class ActividadController {

    @Autowired
    private ActividadService actividadService;

    @Autowired
    private CultivoService cultivoService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("actividades", actividadService.listarActividades());
        model.addAttribute("cultivos", cultivoService.listarCultivos());
        return "dashboard/actividades";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam Long idCultivo,
                         @RequestParam String tipoActividad,
                         @RequestParam(required = false) String descripcion,
                         @RequestParam String fechaActividad,
                         @RequestParam(required = false) String trabajadorResponsable,
                         @RequestParam(required = false) String estado) {
        Actividad actividad = new Actividad();
        
        Cultivo cultivo = cultivoService.obtenerCultivoPorId(idCultivo)
                .orElseThrow(() -> new RuntimeException("Cultivo no encontrado"));
        actividad.setCultivo(cultivo);
        
        actividad.setTipoActividad(tipoActividad);
        actividad.setDescripcion(descripcion);
        actividad.setFechaActividad(LocalDate.parse(fechaActividad));
        actividad.setTrabajadorResponsable(trabajadorResponsable);
        actividad.setEstado(estado != null ? estado : "Pendiente");
        
        actividadService.guardarActividad(actividad);
        return "redirect:/trabajador/actividades";
    }

    @PostMapping("/actualizar")
    public String actualizar(@RequestParam Long idActividad,
                            @RequestParam Long idCultivo,
                            @RequestParam String tipoActividad,
                            @RequestParam(required = false) String descripcion,
                            @RequestParam String fechaActividad,
                            @RequestParam(required = false) String trabajadorResponsable,
                            @RequestParam(required = false) String estado) {
        Actividad actividad = actividadService.obtenerActividadPorId(idActividad)
                .orElseThrow(() -> new RuntimeException("Actividad no encontrada"));
        
        Cultivo cultivo = cultivoService.obtenerCultivoPorId(idCultivo)
                .orElseThrow(() -> new RuntimeException("Cultivo no encontrado"));
        actividad.setCultivo(cultivo);
        
        actividad.setTipoActividad(tipoActividad);
        actividad.setDescripcion(descripcion);
        actividad.setFechaActividad(LocalDate.parse(fechaActividad));
        actividad.setTrabajadorResponsable(trabajadorResponsable);
        if (estado != null) {
            actividad.setEstado(estado);
        }
        
        actividadService.actualizarActividad(actividad);
        return "redirect:/trabajador/actividades";
    }

    @DeleteMapping("/eliminar/{id}")
    @ResponseBody
    public String eliminar(@PathVariable Long id) {
        actividadService.eliminarActividad(id);
        return "{\"status\":\"ok\"}";
    }
}
