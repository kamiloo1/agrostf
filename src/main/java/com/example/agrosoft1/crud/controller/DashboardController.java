package com.example.agrosoft1.crud.controller;

import com.example.agrosoft1.crud.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    /** Redirige al dashboard según el rol (usado tras login con defaultSuccessUrl) */
    @GetMapping
    public RedirectView redirigirPorRol(Authentication auth) {
        if (auth == null || auth.getAuthorities() == null || auth.getAuthorities().isEmpty()) {
            return new RedirectView("/login?error=sin_rol", true);
        }
        String role = auth.getAuthorities().iterator().next().getAuthority();
        if ("ROLE_ADMIN".equals(role)) return new RedirectView("/dashboard/administrador", true);
        if ("ROLE_VETERINARIO".equals(role)) return new RedirectView("/dashboard/veterinario", true);
        if ("ROLE_TRABAJADOR".equals(role)) return new RedirectView("/dashboard/trabajador", true);
        return new RedirectView("/login?error=rol_no_valido", true);
    }

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
        logger.info("Cargando dashboard de administrador");
        model.addAttribute("usuarios", 0);
        model.addAttribute("cultivos", 0);
        model.addAttribute("ganado", 0);
        model.addAttribute("ganadoSaludable", 0);
        model.addAttribute("actividadesPendientes", 0);
        model.addAttribute("totalActividades", 0);
        model.addAttribute("porcentajeGanadoSaludable", 0);
        model.addAttribute("porcentajeActividadesCompletadas", 0);
        try {
            model.addAttribute("usuarios", usuarioService.listarUsuarios().size());
        } catch (Exception e) { logger.warn("usuarios: {}", e.getMessage()); }
        try {
            model.addAttribute("cultivos", cultivoService.listarCultivos().size());
        } catch (Exception e) { logger.warn("cultivos: {}", e.getMessage()); }
        try {
            long ganado = ganadoService.contarGanado();
            long ganadoSaludable = ganadoService.contarGanadoSaludable();
            model.addAttribute("ganado", ganado);
            model.addAttribute("ganadoSaludable", ganadoSaludable);
            int porcentajeGanado = ganado > 0 ? (int) ((ganadoSaludable * 100) / ganado) : 0;
            model.addAttribute("porcentajeGanadoSaludable", porcentajeGanado);
        } catch (Exception e) { logger.warn("ganado: {}", e.getMessage()); }
        try {
            long pendientes = actividadService.contarActividadesPendientes();
            long total = actividadService.listarActividades().size();
            model.addAttribute("actividadesPendientes", pendientes);
            model.addAttribute("totalActividades", total);
            long completadas = Math.max(total - pendientes, 0);
            int porcentajeCompletadas = total > 0 ? (int) ((completadas * 100) / total) : 0;
            model.addAttribute("porcentajeActividadesCompletadas", porcentajeCompletadas);
        } catch (Exception e) { logger.warn("actividades: {}", e.getMessage()); }
        logger.info("Dashboard administrador listo");
        return "dashboard/administrador";
    }

    @GetMapping("/veterinario")
    public String panelVeterinario(Model model) {
        logger.info("Cargando dashboard de veterinario");
        try {
            model.addAttribute("tratamientos", veterinarioService.contarTratamientos());
            model.addAttribute("reportes", veterinarioService.contarReportes());
            model.addAttribute("revisiones", veterinarioService.contarRevisiones());
            model.addAttribute("ganado", ganadoService.contarGanado());
            model.addAttribute("ganadoSaludable", ganadoService.contarGanadoSaludable());
            model.addAttribute("totalGanado", ganadoService.contarGanado());
            logger.info("Dashboard veterinario cargado correctamente");
        } catch (Exception e) {
            logger.error("Error al cargar dashboard veterinario: {}", e.getMessage(), e);
            // Valores por defecto
            model.addAttribute("tratamientos", 0);
            model.addAttribute("reportes", 0);
            model.addAttribute("revisiones", 0);
            model.addAttribute("ganado", 0);
            model.addAttribute("ganadoSaludable", 0);
            model.addAttribute("totalGanado", 0);
        }
        return "dashboard/veterinario";
    }

    @GetMapping("/trabajador")
    public String panelTrabajador(Model model) {
        logger.info("Cargando dashboard de trabajador");
        try {
            model.addAttribute("cultivos", cultivoService.listarCultivos());
            model.addAttribute("actividadesPendientes", actividadService.contarActividadesPendientes());
            model.addAttribute("actividadesCompletadas", actividadService.contarActividadesCompletadas());
            model.addAttribute("totalActividades", actividadService.listarActividades().size());
            logger.info("Dashboard trabajador cargado correctamente");
        } catch (Exception e) {
            logger.error("Error al cargar dashboard trabajador: {}", e.getMessage(), e);
            // Valores por defecto
            model.addAttribute("cultivos", java.util.Collections.emptyList());
            model.addAttribute("actividadesPendientes", 0);
            model.addAttribute("actividadesCompletadas", 0);
            model.addAttribute("totalActividades", 0);
        }
        return "dashboard/trabajador";
    }
}
