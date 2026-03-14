package com.example.agrosoft1.crud.controller;

import com.example.agrosoft1.crud.entity.Usuario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/vet")
public class VetController {

    @GetMapping("/dashboard")
    public String panelVet(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        String nombreUsuario = "Veterinario";
        if (usuario != null) {
            nombreUsuario = (usuario.getNombre() != null && !usuario.getNombre().isEmpty()) 
                ? usuario.getNombre() 
                : usuario.getEmail();
        }
        model.addAttribute("usuario", nombreUsuario);

        // Datos de ejemplo
        model.addAttribute("tratamientos", 5);
        model.addAttribute("reportes", 3);
        model.addAttribute("revisiones", 2);

        return "dashboard/veterinario"; // templates/dashboard/veterinario.html
    }
}
