package com.example.agrosoft1.crud.controller;

import com.example.agrosoft1.crud.entity.Usuario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/trabajador") // Import correcto: org.springframework.web.bind.annotation.RequestMapping
public class TrabajadorController {

    @GetMapping("/dashboard")
    public String panelTrabajador(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        String nombreUsuario = "Trabajador";
        if (usuario != null) {
            nombreUsuario = (usuario.getNombre() != null && !usuario.getNombre().isEmpty()) 
                ? usuario.getNombre() 
                : usuario.getEmail();
        }
        model.addAttribute("usuario", nombreUsuario);

        // Datos de ejemplo
        model.addAttribute("tareas", 8);
        model.addAttribute("riegos", 4);
        model.addAttribute("fumigaciones", 2);

        return "dashboard/trabajador"; // templates/dashboard/trabajador.html
    }
}
