package com.example.agrosoft1.crud.config;

import com.example.agrosoft1.crud.entity.Usuario;
import com.example.agrosoft1.crud.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Añade atributos al modelo en todas las vistas para mostrar menús según el rol.
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired(required = false)
    private UsuarioService usuarioService;

    @ModelAttribute
    public void addRoleToModel(Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                addDefaults(model, "");
                return;
            }
            String role = "";
            try {
                if (auth.getAuthorities() != null) {
                    role = auth.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .findFirst()
                            .orElse("");
                }
            } catch (Exception e) {
                role = "";
            }
            model.addAttribute("userRole", role != null && role.startsWith("ROLE_") ? role.substring(5) : (role != null ? role : ""));
            model.addAttribute("isAdmin", Boolean.valueOf("ROLE_ADMIN".equals(role)));
            model.addAttribute("isVeterinario", Boolean.valueOf("ROLE_VETERINARIO".equals(role)));
            model.addAttribute("isTrabajador", Boolean.valueOf("ROLE_TRABAJADOR".equals(role)));
            String nombreUsuario = "Usuario";
            if (usuarioService != null && auth.getName() != null && !auth.getName().isBlank()) {
                try {
                    nombreUsuario = usuarioService.findByCorreo(auth.getName())
                            .map(Usuario::getNombre)
                            .filter(n -> n != null && !n.isBlank())
                            .orElse(auth.getName().contains("@") ? auth.getName().split("@")[0] : auth.getName());
                } catch (Exception e) {
                    nombreUsuario = auth.getName().contains("@") ? auth.getName().split("@")[0] : auth.getName();
                }
            }
            model.addAttribute("nombreUsuario", nombreUsuario != null ? nombreUsuario : "Usuario");
        } catch (Exception e) {
            addDefaults(model, "Usuario");
        }
    }

    private void addDefaults(Model model, String nombreUsuario) {
        model.addAttribute("userRole", "");
        model.addAttribute("isAdmin", false);
        model.addAttribute("isVeterinario", false);
        model.addAttribute("isTrabajador", false);
        model.addAttribute("nombreUsuario", nombreUsuario != null ? nombreUsuario : "");
    }

    /** Si falla cualquier petición a /cuenta, redirigir a login (no a /cuenta para evitar bucle) */
    @ExceptionHandler(Throwable.class)
    public Object handleCuentaError(Throwable ex, HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri != null && uri.contains("/cuenta")) {
            return new RedirectView("/login?error=cuenta", true);
        }
        if (ex instanceof RuntimeException) throw (RuntimeException) ex;
        throw new RuntimeException(ex);
    }
}
