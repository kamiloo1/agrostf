package com.example.agrosoft1.crud.controller;

import com.example.agrosoft1.crud.service.RecuperacionContrasenaService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
        String urlBase = request.getScheme() + "://" + request.getServerName()
                + (request.getServerPort() == 80 || request.getServerPort() == 443 ? "" : ":" + request.getServerPort())
                + request.getContextPath();
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
}
