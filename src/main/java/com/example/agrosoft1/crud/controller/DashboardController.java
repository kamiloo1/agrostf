package com.example.agrosoft1.crud.controller;

import com.example.agrosoft1.crud.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CultivoService cultivoService;

    @Autowired
    private VeterinarioService veterinarioService;

    @Autowired
    private GanadoService ganadoService;

    @Autowired
    private ActividadService actividadService;

    @GetMapping("/administrador")
    public String panelAdministrador(Model model) {
        model.addAttribute("usuarios", usuarioService.listarUsuarios().size());
        model.addAttribute("cultivos", cultivoService.listarCultivos().size());
        model.addAttribute("ganado", ganadoService.contarGanado());
        model.addAttribute("ganadoSaludable", ganadoService.contarGanadoSaludable());
        model.addAttribute("actividadesPendientes", actividadService.contarActividadesPendientes());
        model.addAttribute("totalActividades", actividadService.listarActividades().size());
        return "dashboard/administrador";
    }

    @GetMapping("/veterinario")
    public String panelVeterinario(Model model) {
        model.addAttribute("tratamientos", veterinarioService.contarTratamientos());
        model.addAttribute("reportes", veterinarioService.contarReportes());
        model.addAttribute("revisiones", veterinarioService.contarRevisiones());
        model.addAttribute("ganado", ganadoService.contarGanado());
        model.addAttribute("ganadoSaludable", ganadoService.contarGanadoSaludable());
        model.addAttribute("totalGanado", ganadoService.contarGanado());
        return "dashboard/veterinario";
    }

    @GetMapping("/trabajador")
    public String panelTrabajador(Model model) {
        model.addAttribute("cultivos", cultivoService.listarCultivos());
        model.addAttribute("actividadesPendientes", actividadService.contarActividadesPendientes());
        model.addAttribute("actividadesCompletadas", actividadService.contarActividadesCompletadas());
        model.addAttribute("totalActividades", actividadService.listarActividades().size());
        return "dashboard/trabajador";
    }
}
