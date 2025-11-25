package com.example.agrosoft1.crud.controller;

import com.example.agrosoft1.crud.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador para gestión de envío masivo de correos.
 * 
 * Permite a los usuarios (especialmente administradores) enviar
 * correos masivos a múltiples destinatarios.
 */
@Controller
@RequestMapping("/admin/correos")
public class EmailController {
    
    @Autowired
    private EmailService emailService;
    
    /**
     * Muestra el formulario para envío masivo de correos
     */
    @GetMapping
    public String mostrarFormulario(Model model) {
        return "admin/correos";
    }
    
    /**
     * Procesa el envío masivo de correos
     * 
     * @param correos Lista de correos separados por comas o saltos de línea
     * @param asunto Asunto del correo
     * @param mensaje Contenido del mensaje
     * @param redirectAttributes Para mostrar mensajes de éxito/error
     * @return Redirección al formulario
     */
    @PostMapping("/enviar")
    public String enviarCorreosMasivos(
            @RequestParam String correos,
            @RequestParam String asunto,
            @RequestParam String mensaje,
            RedirectAttributes redirectAttributes) {
        
        // Validaciones
        if (correos == null || correos.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Debe ingresar al menos un correo electrónico");
            return "redirect:/admin/correos";
        }
        
        if (asunto == null || asunto.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El asunto es obligatorio");
            return "redirect:/admin/correos";
        }
        
        if (mensaje == null || mensaje.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El mensaje es obligatorio");
            return "redirect:/admin/correos";
        }
        
        // Procesar lista de correos (separados por comas, punto y coma, o saltos de línea)
        List<String> listaCorreos = Arrays.stream(correos.split("[,\\n;]"))
                .map(String::trim)
                .filter(email -> !email.isEmpty())
                .filter(email -> email.matches("^[A-Za-z0-9+_.-]+@(.+)$"))
                .collect(Collectors.toList());
        
        if (listaCorreos.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No se encontraron correos electrónicos válidos");
            return "redirect:/admin/correos";
        }
        
        // Enviar correos
        int enviados = emailService.enviarCorreosMasivos(listaCorreos, asunto, mensaje);
        
        if (enviados > 0) {
            redirectAttributes.addFlashAttribute("success", 
                String.format("Se enviaron %d correo(s) exitosamente de %d destinatario(s)", 
                    enviados, listaCorreos.size()));
        } else {
            redirectAttributes.addFlashAttribute("error", 
                "No se pudo enviar ningún correo. Verifique la configuración del servidor de correo.");
        }
        
        return "redirect:/admin/correos";
    }
    
    /**
     * Endpoint REST para envío masivo de correos (API)
     */
    @PostMapping("/api/enviar")
    @ResponseBody
    public String enviarCorreosMasivosAPI(
            @RequestParam String correos,
            @RequestParam String asunto,
            @RequestParam String mensaje) {
        
        List<String> listaCorreos = Arrays.stream(correos.split("[,\\n;]"))
                .map(String::trim)
                .filter(email -> !email.isEmpty())
                .filter(email -> email.matches("^[A-Za-z0-9+_.-]+@(.+)$"))
                .collect(Collectors.toList());
        
        int enviados = emailService.enviarCorreosMasivos(listaCorreos, asunto, mensaje);
        
        return String.format("{\"status\":\"ok\",\"enviados\":%d,\"total\":%d}", 
            enviados, listaCorreos.size());
    }
}

