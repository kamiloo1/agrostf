package com.example.agrosoft1.crud.controller;

import com.example.agrosoft1.crud.entity.Cultivo;
import com.example.agrosoft1.crud.service.CultivoService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.agrosoft1.crud.util.PdfExporter;
import com.example.agrosoft1.crud.util.ExcelExporter;

@Controller
@RequestMapping("/admin/cultivos")
public class CultivoController {

    private final CultivoService cultivoService;

    public CultivoController(CultivoService cultivoService) {
        this.cultivoService = cultivoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("cultivos", cultivoService.listarCultivos());
        model.addAttribute("usuario", "Administrador");
        return "dashboard/cultivos";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam String nombre,
            @RequestParam String tipo,
            @RequestParam String area,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String fechaSiembra,
            @RequestParam(required = false) String fechaCosecha,
            @RequestParam(required = false) String observaciones) {
        Cultivo cultivo = new Cultivo(null, nombre, tipo, area);
        cultivo.setEstado(estado != null ? estado : "Activo");
        if (fechaSiembra != null && !fechaSiembra.isEmpty()) {
            cultivo.setFechaSiembra(java.time.LocalDate.parse(fechaSiembra));
        }
        if (fechaCosecha != null && !fechaCosecha.isEmpty()) {
            cultivo.setFechaCosecha(java.time.LocalDate.parse(fechaCosecha));
        }
        cultivo.setObservaciones(observaciones);
        cultivoService.guardarCultivo(cultivo);
        return "redirect:/admin/cultivos";
    }

    @PostMapping("/actualizar")
    public String actualizar(@RequestParam Long id,
            @RequestParam String nombre,
            @RequestParam String tipo,
            @RequestParam String area,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String fechaSiembra,
            @RequestParam(required = false) String fechaCosecha,
            @RequestParam(required = false) String observaciones) {
        Cultivo cultivo = cultivoService.obtenerCultivoPorId(id)
                .orElseThrow(() -> new RuntimeException("Cultivo no encontrado"));
        
        cultivo.setId(id); // Asegurar que el ID esté establecido
        cultivo.setNombre(nombre);
        cultivo.setTipo(tipo);
        cultivo.setArea(area);
        if (estado != null) {
            cultivo.setEstado(estado);
        }
        if (fechaSiembra != null && !fechaSiembra.isEmpty()) {
            cultivo.setFechaSiembra(java.time.LocalDate.parse(fechaSiembra));
        }
        if (fechaCosecha != null && !fechaCosecha.isEmpty()) {
            cultivo.setFechaCosecha(java.time.LocalDate.parse(fechaCosecha));
        }
        cultivo.setObservaciones(observaciones);
        
        cultivoService.actualizarCultivo(cultivo);
        return "redirect:/admin/cultivos";
    }

    @DeleteMapping("/eliminar/{id}")
    @ResponseBody
    public String eliminar(@PathVariable Long id) {
        cultivoService.eliminarCultivo(id);
        return "{\"status\":\"ok\"}";
    }

    @PostMapping("/cambiar-estado/{id}")
    @ResponseBody
    public String cambiarEstado(@PathVariable Long id, @RequestBody java.util.Map<String, Boolean> request) {
        Boolean activo = request.get("activo");
        cultivoService.cambiarEstadoCultivo(id, activo);
        return "{\"status\":\"ok\"}";
    }

    @GetMapping("/exportar-pdf")
    public ResponseEntity<byte[]> exportarPdf() {
        try {
            byte[] pdf = PdfExporter.exportarCultivosEstadisticas(cultivoService.listarCultivos());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=estadisticas_cultivos.pdf");
            headers.setContentLength(pdf.length);
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            return ResponseEntity.ok().headers(headers).body(pdf);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al exportar PDF de cultivos: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportarExcel() {
        try {
            byte[] excel = ExcelExporter.exportarCultivos(cultivoService.listarCultivos());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=cultivos.xlsx");
            headers.setContentLength(excel.length);
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            return ResponseEntity.ok().headers(headers).body(excel);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al exportar Excel de cultivos: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}
