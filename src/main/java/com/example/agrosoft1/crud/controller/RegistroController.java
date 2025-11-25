package com.example.agrosoft1.crud.controller;

import com.example.agrosoft1.crud.entity.Usuario;
import com.example.agrosoft1.crud.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/registro")
public class RegistroController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String mostrarFormulario() {
        return "registrarse";
    }

    @PostMapping("/guardar")
    public String registrarUsuario(@RequestParam String email,
                                  @RequestParam String numeroDocumento,
                                  @RequestParam String contrasena,
                                  Model model) {
        try {
            // Validar que el correo no exista
            if (usuarioService.findByCorreo(email).isPresent()) {
                model.addAttribute("error", "El correo electrónico ya está registrado");
                return "registrarse";
            }

            // Validar que el número de documento no exista
            if (usuarioService.findByNumeroDocumento(numeroDocumento).isPresent()) {
                model.addAttribute("error", "El número de documento ya está registrado");
                return "registrarse";
            }

            // Crear nuevo usuario
            Usuario usuario = new Usuario();
            usuario.setNombre(null); // Nombre será asignado por el administrador
            usuario.setCorreo(email);
            usuario.setNumeroDocumento(numeroDocumento);
            usuario.setPassword(contrasena);
            usuario.setRole(null); // El rol será asignado por el administrador
            usuario.setActivo(true); // Por defecto activo
            usuario.setFechaCreacion(LocalDateTime.now());
            
            usuarioService.guardarUsuario(usuario);
            model.addAttribute("mensaje", "Usuario registrado exitosamente. El administrador asignará su rol.");
            return "redirect:/login?registroexitoso=true";
        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar usuario: " + e.getMessage());
            return "registrarse";
        }
    }
}
