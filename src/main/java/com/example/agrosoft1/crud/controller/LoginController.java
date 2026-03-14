package com.example.agrosoft1.crud.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/login")
public class LoginController {

    // -------------------------------
    // Mostrar formulario de login
    // -------------------------------
    @GetMapping
    public String mostrarLogin(Model model, 
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String expired) {
        if (expired != null) {
            model.addAttribute("error", "Su sesión ha expirado o la navegación fue bloqueada. Por favor, inicie sesión nuevamente.");
        } else if (error != null) {
            switch (error) {
                case "true":
                    model.addAttribute("error", "Correo o contraseña incorrectos");
                    break;
                case "sin_rol":
                    model.addAttribute("error", "Usuario sin rol asignado. Contacte al administrador.");
                    break;
                case "rol_no_valido":
                    model.addAttribute("error", "Rol no válido. Contacte al administrador.");
                    break;
                case "error_sistema":
                    model.addAttribute("error", "Error en el sistema. Intente nuevamente.");
                    break;
                case "no_autenticado":
                    model.addAttribute("error", "Debe iniciar sesión para acceder a esta página.");
                    break;
                case "sin_permisos":
                    model.addAttribute("error", "No tiene permisos para acceder a esta página. Contacte al administrador.");
                    break;
                case "expired":
                case "expired=true":
                    model.addAttribute("error", "Su sesión ha expirado. Por favor, inicie sesión nuevamente.");
                    break;
                case "inactivo":
                    model.addAttribute("error", "Usuario inactivo. En phpMyAdmin (base agrostf, tabla usuarios) ponga activo=1 para admin@agrosoft.local.");
                    break;
                case "cuenta":
                    model.addAttribute("error", "No se pudo cargar Mi cuenta. Compruebe que MySQL est? en ejecuci?n e inicie sesi?n de nuevo.");
                    break;
                default:
                    model.addAttribute("error", "Error al iniciar sesión. Verifique sus credenciales.");
            }
        }
        return "login"; // login.html en templates
    }

    // -------------------------------
    // Procesar login (ahora manejado por Spring Security)
    // -------------------------------
    @GetMapping("/success")
    public String loginSuccess(HttpSession session) {
        // Spring Security maneja la autenticación y redirección por rol
        // Este endpoint ya no es necesario, pero se mantiene por compatibilidad
        return "redirect:/dashboard/administrador";
    }
}
