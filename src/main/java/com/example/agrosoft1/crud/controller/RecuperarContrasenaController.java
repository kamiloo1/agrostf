package com.example.agrosoft1.crud.controller;

import com.example.agrosoft1.crud.service.RecuperacionContrasenaService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/recuperar")
public class RecuperarContrasenaController {

    private final RecuperacionContrasenaService recuperacionService;

    public RecuperarContrasenaController(RecuperacionContrasenaService recuperacionService) {
        this.recuperacionService = recuperacionService;
    }

    @GetMapping
    public String formularioSolicitud(Model model) {
        return "recuperar/solicitud";
    }

    @PostMapping
    public String procesarSolicitud(@RequestParam("correo") String correo,
                                   HttpServletRequest request,
                                   RedirectAttributes redirectAttributes) {
        if (correo == null || correo.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Indica tu correo electrónico.");
            return "redirect:/recuperar";
        }
        String urlBase = construirUrlBasePublica(request);
        recuperacionService.solicitarRecuperacion(correo.trim(), urlBase);
        redirectAttributes.addFlashAttribute("success", "Si ese correo está registrado, recibirás un enlace para restablecer tu contraseña. Revisa tu bandeja de entrada.");
        return "redirect:/recuperar";
    }

    @GetMapping("/restablecer")
    public String formularioRestablecer(@RequestParam(value = "token", required = false) String token, Model model) {
        if (!recuperacionService.tokenValido(token)) {
            model.addAttribute("tokenInvalido", true);
            return "recuperar/restablecer";
        }
        model.addAttribute("token", token);
        return "recuperar/restablecer";
    }

    @PostMapping("/restablecer")
    public String procesarRestablecer(@RequestParam("token") String token,
                                      @RequestParam("contrasenaNueva") String contrasenaNueva,
                                      @RequestParam("contrasenaConfirmar") String contrasenaConfirmar,
                                      RedirectAttributes redirectAttributes) {
        if (!recuperacionService.tokenValido(token)) {
            redirectAttributes.addFlashAttribute("error", "El enlace ha expirado o no es válido. Solicita uno nuevo.");
            return "redirect:/recuperar";
        }
        if (contrasenaNueva == null || contrasenaNueva.length() < 6) {
            redirectAttributes.addFlashAttribute("error", "La contraseña debe tener al menos 6 caracteres.");
            redirectAttributes.addAttribute("token", token);
            return "redirect:/recuperar/restablecer";
        }
        if (!contrasenaNueva.equals(contrasenaConfirmar)) {
            redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden.");
            redirectAttributes.addAttribute("token", token);
            return "redirect:/recuperar/restablecer";
        }
        if (recuperacionService.restablecerConToken(token, contrasenaNueva)) {
            redirectAttributes.addFlashAttribute("success", "Contraseña actualizada. Ya puedes iniciar sesión.");
            return "redirect:/login";
        }
        redirectAttributes.addFlashAttribute("error", "No se pudo restablecer la contraseña. El enlace puede haber expirado.");
        return "redirect:/recuperar";
    }

    private String construirUrlBasePublica(HttpServletRequest request) {
        String proto = obtenerPrimero(request.getHeader("X-Forwarded-Proto"));
        String host = obtenerPrimero(request.getHeader("X-Forwarded-Host"));
        String port = obtenerPrimero(request.getHeader("X-Forwarded-Port"));

        String scheme = StringUtils.hasText(proto) ? proto : request.getScheme();
        String serverHost = StringUtils.hasText(host) ? host : request.getServerName();

        // Si X-Forwarded-Host ya incluye puerto, no duplicarlo.
        if (StringUtils.hasText(serverHost) && serverHost.contains(":")) {
            return scheme + "://" + serverHost + request.getContextPath();
        }

        int defaultPort = "https".equalsIgnoreCase(scheme) ? 443 : 80;
        int detectedPort = request.getServerPort();
        if (StringUtils.hasText(port)) {
            try {
                detectedPort = Integer.parseInt(port);
            } catch (NumberFormatException ignored) {
                // Conserva el puerto detectado por el servlet container.
            }
        }

        String portPart = detectedPort == defaultPort ? "" : ":" + detectedPort;
        return scheme + "://" + serverHost + portPart + request.getContextPath();
    }

    private String obtenerPrimero(String header) {
        if (!StringUtils.hasText(header)) {
            return null;
        }
        int comma = header.indexOf(',');
        return comma >= 0 ? header.substring(0, comma).trim() : header.trim();
    }
}
