package com.example.agrosoft1.crud.controller;

import com.example.agrosoft1.crud.entity.Usuario;
import com.example.agrosoft1.crud.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.example.agrosoft1.crud.util.ExcelExporter;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    // -------------------------------
    // GESTIÓN DE USUARIOS
    // -------------------------------
    @GetMapping("/usuarios")
    public String gestionUsuarios(Model model) {
        model.addAttribute("usuariosLista", usuarioService.listarUsuarios());
        model.addAttribute("usuarios", usuarioService.listarUsuarios().size());
        return "admin/usuarios"; // ← corregido para que coincida con la carpeta
    }

    // -------------------------------
    // CREAR USUARIO
    // -------------------------------
    @PostMapping("/usuarios/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario) {
        usuarioService.guardarUsuario(usuario);
        return "redirect:/admin/usuarios";
    }

    // -------------------------------
    // ACTUALIZAR USUARIO
    // -------------------------------
    @PostMapping("/usuarios/actualizar")
    public String actualizarUsuario(@ModelAttribute Usuario usuario) {
        System.out.println("ID recibido para actualizar: " + usuario.getId());
        usuarioService.actualizarUsuario(usuario);
        return "redirect:/admin/usuarios";
    }

    // -------------------------------
    // ELIMINAR USUARIO
    // -------------------------------
    @DeleteMapping("/usuarios/eliminar/{id}")
    @ResponseBody
    public String eliminarUsuario(@PathVariable Integer id) {
        usuarioService.eliminarUsuario(id);
        return "{\"status\":\"ok\"}";
    }

    // -------------------------------
    // EXPORTAR USUARIOS A EXCEL
    // -------------------------------
    @GetMapping("/usuarios/exportar")
    public ResponseEntity<byte[]> exportarUsuarios() {
        byte[] excel = ExcelExporter.exportarUsuarios(usuarioService.listarUsuarios());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=usuarios.xlsx");
        headers.setContentLength(excel.length);
        return ResponseEntity.ok().headers(headers).body(excel);
    }
}