package com.example.agrosoft1.crud.controller;

import com.example.agrosoft1.crud.entity.Usuario;
import com.example.agrosoft1.crud.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para "Mi cuenta": perfil del usuario logueado y cambio de contraseña.
 */
@Controller
@RequestMapping("/cuenta")
public class CuentaController {

    private final UsuarioService usuarioService;

    public CuentaController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /** Vista principal "Mi cuenta": datos del perfil y enlace a cambiar contraseña */
    @GetMapping
    public String miCuenta(org.springframework.security.core.Authentication auth, Model model) {
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            return "redirect:/login";
        }
        try {
            return usuarioService.findByCorreo(auth.getName())
                    .map(usuario -> {
                        Map<String, String> datos = new HashMap<>();
                        datos.put("nombre", usuario.getNombre() != null && !usuario.getNombre().isBlank() ? usuario.getNombre() : "No indicado");
                        datos.put("correo", usuario.getCorreo() != null ? usuario.getCorreo() : "");
                        datos.put("telefono", usuario.getTelefono() != null && !usuario.getTelefono().isBlank() ? usuario.getTelefono() : "No indicado");
                        model.addAttribute("usuario", datos);
                        String r = "Usuario";
                        if (usuario.getRole() != null && usuario.getRole().getNombre() != null) {
                            r = nombreRolAmigable(usuario.getRole().getNombre());
                        }
                        model.addAttribute("rolNombre", r);
                        if (!model.containsAttribute("success")) model.addAttribute("success", null);
                        if (!model.containsAttribute("error")) model.addAttribute("error", null);
                        return "cuenta/index";
                    })
                    .orElse("redirect:/login");
        } catch (Exception e) {
            return "redirect:/login?error=cuenta";
        }
    }

    @GetMapping("/editar")
    public String formularioEditarPerfil(Authentication auth, Model model) {
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            return "redirect:/login";
        }
        try {
            return usuarioService.findByCorreo(auth.getName())
                    .map(usuario -> {
                        model.addAttribute("nombre", usuario.getNombre() != null ? usuario.getNombre() : "");
                        model.addAttribute("telefono", usuario.getTelefono() != null ? usuario.getTelefono() : "");
                        model.addAttribute("correo", usuario.getCorreo());
                        return "cuenta/editar";
                    })
                    .orElse("redirect:/login");
        } catch (Exception e) {
            return "redirect:/cuenta";
        }
    }

    @PostMapping("/editar")
    public String guardarPerfil(
            Authentication auth,
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "telefono", required = false) String telefono,
            RedirectAttributes redirectAttributes) {
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            return "redirect:/login";
        }
        try {
            Usuario usuario = usuarioService.findByCorreo(auth.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
            usuario.setNombre(nombre != null && !nombre.isBlank() ? nombre.trim() : null);
            usuario.setTelefono(telefono != null ? telefono.trim() : null);
            usuarioService.actualizarUsuario(usuario);
            redirectAttributes.addFlashAttribute("success", "Perfil actualizado correctamente.");
            return "redirect:/cuenta";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/cuenta/editar";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo actualizar el perfil. Intente de nuevo.");
            return "redirect:/cuenta/editar";
        }
    }

    private static String nombreRolAmigable(String rol) {
        if (rol == null) return "Usuario";
        if (rol.contains("ADMIN")) return "Administrador";
        if (rol.contains("VETERINARIO")) return "Veterinario";
        if (rol.contains("TRABAJADOR")) return "Trabajador agrícola";
        return rol.replace("ROLE_", "");
    }

    @GetMapping("/cambiar-password")
    public String formularioCambiarPassword(Authentication auth, Model model) {
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            return "redirect:/login";
        }
        try {
            String correo = auth.getName();
            model.addAttribute("correo", correo != null ? correo : "");
            if (!model.containsAttribute("error")) model.addAttribute("error", null);
            if (!model.containsAttribute("success")) model.addAttribute("success", null);
            return "cuenta/cambiar-password";
        } catch (Exception e) {
            return "redirect:/cuenta";
        }
    }

    @PostMapping("/cambiar-password")
    public String procesarCambiarPassword(
            Authentication auth,
            @RequestParam("contrasenaActual") String contrasenaActual,
            @RequestParam("contrasenaNueva") String contrasenaNueva,
            @RequestParam("contrasenaConfirmar") String contrasenaConfirmar,
            RedirectAttributes redirectAttributes) {
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            return "redirect:/login";
        }
        String correo = auth.getName();
        if (contrasenaNueva == null || !contrasenaNueva.equals(contrasenaConfirmar)) {
            redirectAttributes.addFlashAttribute("error", "La contraseña nueva y la confirmación no coinciden.");
            return "redirect:/cuenta/cambiar-password";
        }
        try {
            usuarioService.cambiarContrasena(correo, contrasenaActual, contrasenaNueva);
            redirectAttributes.addFlashAttribute("success", "Contraseña actualizada correctamente.");
            return "redirect:/cuenta/cambiar-password";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/cuenta/cambiar-password";
        }
    }
}
