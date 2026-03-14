package com.example.agrosoft1.crud.controller;

import com.example.agrosoft1.crud.entity.Usuario;
import com.example.agrosoft1.crud.repository.RoleRepository;
import com.example.agrosoft1.crud.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.example.agrosoft1.crud.util.ExcelExporter;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private RoleRepository roleRepository;

    // -------------------------------
    // GESTIÓN DE USUARIOS
    // -------------------------------
    @GetMapping("/usuarios")
    public String gestionUsuarios(Model model) {
        try {
            var usuarios = usuarioService.listarUsuarios();
            model.addAttribute("usuariosLista", usuarios);
            model.addAttribute("usuarios", usuarios.size());
            model.addAttribute("roles", roleRepository.findAll());
            return "admin/usuarios";
        } catch (Exception e) {
            model.addAttribute("usuariosLista", java.util.Collections.emptyList());
            model.addAttribute("usuarios", 0);
            model.addAttribute("roles", roleRepository.findAll());
            return "admin/usuarios";
        }
    }

    // -------------------------------
    // CREAR USUARIO
    // -------------------------------
    @PostMapping("/usuarios/guardar")
    public String guardarUsuario(@Valid @ModelAttribute Usuario usuario, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            String mensaje = bindingResult.getFieldErrors().stream()
                .map(e -> e.getDefaultMessage())
                .findFirst()
                .orElse("Revise los datos del formulario.");
            redirectAttributes.addFlashAttribute("errorValidacion", mensaje);
            return "redirect:/admin/usuarios";
        }
        try {
            usuarioService.guardarUsuario(usuario);
            redirectAttributes.addFlashAttribute("success", "Usuario creado correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorValidacion", e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    // -------------------------------
    // ACTUALIZAR USUARIO
    // -------------------------------
    @PostMapping("/usuarios/actualizar")
    public String actualizarUsuario(@Valid @ModelAttribute Usuario usuario, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            String mensaje = bindingResult.getFieldErrors().stream()
                .map(e -> e.getDefaultMessage())
                .findFirst()
                .orElse("Revise los datos del formulario.");
            redirectAttributes.addFlashAttribute("errorValidacion", mensaje);
            return "redirect:/admin/usuarios";
        }
        try {
            usuarioService.actualizarUsuario(usuario);
            redirectAttributes.addFlashAttribute("success", "Usuario actualizado correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorValidacion", e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    // -------------------------------
    // ELIMINAR USUARIO (DEPRECADO - No se eliminan registros)
    // -------------------------------
    @DeleteMapping("/usuarios/eliminar/{id}")
    @ResponseBody
    public String eliminarUsuario(@PathVariable Integer id) {
        usuarioService.eliminarUsuario(id);
        return "{\"status\":\"ok\"}";
    }

    // -------------------------------
    // CAMBIAR ESTADO DE USUARIO (ACTIVAR/DESACTIVAR)
    // -------------------------------
    @PostMapping("/usuarios/cambiar-estado/{id}")
    @ResponseBody
    public String cambiarEstadoUsuario(@PathVariable Integer id, @RequestBody java.util.Map<String, Boolean> request) {
        Boolean activo = request.get("activo");
        usuarioService.cambiarEstadoUsuario(id, activo);
        return "{\"status\":\"ok\"}";
    }

    // -------------------------------
    // EXPORTAR USUARIOS A EXCEL
    // -------------------------------
    @GetMapping("/usuarios/exportar")
    public ResponseEntity<byte[]> exportarUsuarios() {
        try {
            byte[] excel = ExcelExporter.exportarUsuarios(usuarioService.listarUsuarios());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=usuarios.xlsx");
            headers.setContentLength(excel.length);
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            return ResponseEntity.ok().headers(headers).body(excel);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}