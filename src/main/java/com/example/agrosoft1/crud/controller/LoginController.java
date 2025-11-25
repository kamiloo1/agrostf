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
    public String mostrarLogin() {
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

    @GetMapping("/error")
    public String loginError(Model model) {
        model.addAttribute("error", "Correo o contraseña incorrectos");
        return "login";
    }
}
