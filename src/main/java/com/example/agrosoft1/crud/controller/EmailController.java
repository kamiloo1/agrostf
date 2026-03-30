package com.example.agrosoft1.crud.controller;

import com.example.agrosoft1.crud.entity.PlantillaCorreo;
import com.example.agrosoft1.crud.entity.Usuario;
import com.example.agrosoft1.crud.repository.PlantillaCorreoRepository;
import com.example.agrosoft1.crud.repository.UsuarioRepository;
import com.example.agrosoft1.crud.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controlador para gestión de envío masivo de correos.
 * 
 * Permite a los usuarios (especialmente administradores) enviar
 * correos masivos a múltiples destinatarios usando plantillas predeterminadas
 * almacenadas en la base de datos.
 */
@Controller
@RequestMapping("/admin/correos")
public class EmailController {
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private PlantillaCorreoRepository plantillaCorreoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    /**
     * Muestra el formulario para envío masivo de correos con plantillas disponibles
     */
    @GetMapping
    public String mostrarFormulario(Model model) {
        try {
            // Cargar todas las plantillas activas
            List<PlantillaCorreo> plantillas = plantillaCorreoRepository.findByActivoTrue();
            model.addAttribute("plantillas", plantillas);
        } catch (Exception e) {
            // Si hay error (probablemente la tabla no existe), usar lista vacía
            model.addAttribute("plantillas", java.util.Collections.emptyList());
            model.addAttribute("errorPlantillas", "Las plantillas no están disponibles. La tabla aún no ha sido creada.");
        }
        
        // Cargar todos los usuarios activos para selección rápida
        try {
            List<Usuario> usuarios = usuarioRepository.findAll().stream()
                    .filter(u -> u.getActivo() != null && u.getActivo())
                    .filter(u -> u.getCorreo() != null && !u.getCorreo().trim().isEmpty())
                    .collect(Collectors.toList());
            model.addAttribute("usuarios", usuarios);
        } catch (Exception e) {
            model.addAttribute("usuarios", java.util.Collections.emptyList());
        }
        
        return "admin/correos";
    }
    
    /**
     * Endpoint REST para obtener una plantilla por ID (404 si no existe)
     */
    @GetMapping("/api/plantilla/{id}")
    @ResponseBody
    public ResponseEntity<PlantillaCorreo> obtenerPlantilla(@PathVariable Integer id) {
        if (id == null) {
            return ResponseEntity.badRequest().build();
        }
        return plantillaCorreoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Procesa el envío masivo de correos
     * 
     * @param correos Lista de correos separados por comas o saltos de línea
     * @param asunto Asunto del correo
     * @param mensaje Contenido del mensaje
     * @param plantillaId ID de la plantilla seleccionada (opcional)
     * @param redirectAttributes Para mostrar mensajes de éxito/error
     * @return Redirección al formulario
     */
    @PostMapping("/enviar")
    public String enviarCorreosMasivos(
            @RequestParam String correos,
            @RequestParam(required = false) String asunto,
            @RequestParam(required = false) String mensaje,
            @RequestParam(required = false) Integer plantillaId,
            RedirectAttributes redirectAttributes) {
        
        // Validaciones
        if (correos == null || correos.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Debe ingresar al menos un correo electrónico");
            return "redirect:/admin/correos";
        }
        
        String asuntoFinal = asunto;
        String mensajeFinal = mensaje;
        
        // Si se seleccionó una plantilla, usar sus valores
        if (plantillaId != null) {
            Optional<PlantillaCorreo> plantillaOpt = plantillaCorreoRepository.findById(plantillaId);
            if (plantillaOpt.isPresent()) {
                PlantillaCorreo plantilla = plantillaOpt.get();
                // Usar plantilla solo si no se proporcionaron valores personalizados
                if (asunto == null || asunto.trim().isEmpty()) {
                    asuntoFinal = plantilla.getAsunto();
                }
                if (mensaje == null || mensaje.trim().isEmpty()) {
                    mensajeFinal = plantilla.getMensaje();
                }
            }
        }
        
        if (asuntoFinal == null || asuntoFinal.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El asunto es obligatorio");
            return "redirect:/admin/correos";
        }
        
        if (mensajeFinal == null || mensajeFinal.trim().isEmpty()) {
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
        
        // Enviar en segundo plano para evitar que el navegador quede cargando.
        emailService.enviarCorreosMasivosAsync(listaCorreos, asuntoFinal, mensajeFinal);
        redirectAttributes.addFlashAttribute("success",
                String.format("Envío iniciado para %d destinatario(s). Revisa los logs para ver el resultado.", listaCorreos.size()));
        
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

