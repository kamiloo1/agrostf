package com.example.agrosoft1.crud.controller;

import com.example.agrosoft1.crud.entity.Ganado;
import com.example.agrosoft1.crud.service.GanadoService;
import com.example.agrosoft1.crud.util.PdfExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/ganado")
public class GanadoController {

    private static final Logger logger = LoggerFactory.getLogger(GanadoController.class);

    @Autowired
    private GanadoService ganadoService;

    @GetMapping
    public String listar(Model model) {
        try {
            logger.info("=== Cargando página de ganado ===");
            List<Ganado> ganado = ganadoService.listarGanado();
            if (ganado == null) {
                ganado = new java.util.ArrayList<>();
                logger.warn("La lista de ganado es null, inicializando lista vacía");
            }
            logger.info("Ganado encontrado en BD: {} registros", ganado.size());
            
            // Log de los primeros registros para debug
            if (!ganado.isEmpty()) {
                logger.info("Primeros 3 registros:");
                for (int i = 0; i < Math.min(3, ganado.size()); i++) {
                    Ganado g = ganado.get(i);
                    logger.info("  - ID: {}, Tipo: {}, Raza: {}, Estado: {}, Activo: {}", 
                        g.getIdGanado(), g.getTipo(), g.getRaza(), g.getEstadoSalud(), g.getActivo());
                }
            } else {
                logger.warn("⚠️ La lista de ganado está VACÍA. No hay registros en la base de datos.");
            }
            
            model.addAttribute("ganado", ganado);
            logger.info("Datos agregados al modelo. Total: {}", ganado.size());
            return "dashboard/ganado";
        } catch (Exception e) {
            logger.error("Error al cargar página de ganado: {}", e.getMessage(), e);
            e.printStackTrace();
            model.addAttribute("ganado", new java.util.ArrayList<>());
            model.addAttribute("error", "Error al cargar los datos: " + e.getMessage());
            return "dashboard/ganado";
        }
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam String tipo,
                         @RequestParam(required = false) String raza,
                         @RequestParam(required = false) Integer edad,
                         @RequestParam(required = false) Double peso,
                         @RequestParam(required = false) String estadoSalud,
                         @RequestParam(required = false) String fechaNacimiento) {
        try {
            logger.info("=== Guardando nuevo ganado ===");
            logger.info("Tipo: {}, Raza: {}, Edad: {}, Peso: {}, Estado: {}", tipo, raza, edad, peso, estadoSalud);
            
            Ganado ganado = new Ganado();
            ganado.setTipo(tipo);
            ganado.setRaza(raza);
            ganado.setEdad(edad);
            ganado.setPeso(peso);
            ganado.setEstadoSalud(estadoSalud != null && !estadoSalud.isEmpty() ? estadoSalud : "Saludable");
            ganado.setActivo(true);
            ganado.setFechaCreacion(java.time.LocalDateTime.now());
            if (fechaNacimiento != null && !fechaNacimiento.isEmpty()) {
                ganado.setFechaNacimiento(LocalDate.parse(fechaNacimiento));
            }
            
            Ganado guardado = ganadoService.guardarGanado(ganado);
            logger.info("✓ Ganado guardado exitosamente. ID: {}", guardado.getIdGanado());
            return "redirect:/admin/ganado?success=guardado";
        } catch (Exception e) {
            logger.error("✗ Error al guardar ganado: {}", e.getMessage(), e);
            e.printStackTrace();
            return "redirect:/admin/ganado?error=" + java.net.URLEncoder.encode(e.getMessage(), java.nio.charset.StandardCharsets.UTF_8);
        }
    }

    @PostMapping("/actualizar")
    public String actualizar(@RequestParam Long idGanado,
                            @RequestParam String tipo,
                            @RequestParam(required = false) String raza,
                            @RequestParam(required = false) Integer edad,
                            @RequestParam(required = false) Double peso,
                            @RequestParam(required = false) String estadoSalud,
                            @RequestParam(required = false) String fechaNacimiento,
                            @RequestParam(required = false, defaultValue = "true") Boolean activo) {
        try {
            logger.info("=== Actualizando ganado ID: {} ===", idGanado);
            
            Ganado ganado = ganadoService.obtenerGanadoPorId(idGanado)
                    .orElseThrow(() -> new RuntimeException("Ganado no encontrado con ID: " + idGanado));
            
            ganado.setTipo(tipo);
            ganado.setRaza(raza);
            ganado.setEdad(edad);
            ganado.setPeso(peso);
            ganado.setEstadoSalud(estadoSalud != null && !estadoSalud.isEmpty() ? estadoSalud : "Saludable");
            if (fechaNacimiento != null && !fechaNacimiento.isEmpty()) {
                ganado.setFechaNacimiento(LocalDate.parse(fechaNacimiento));
            }
            ganado.setActivo(activo != null ? activo : Boolean.TRUE);
            
            ganadoService.actualizarGanado(ganado);
            logger.info("✓ Ganado actualizado exitosamente. ID: {}", idGanado);
            return "redirect:/admin/ganado?success=actualizado";
        } catch (Exception e) {
            logger.error("✗ Error al actualizar ganado: {}", e.getMessage(), e);
            e.printStackTrace();
            return "redirect:/admin/ganado?error=" + java.net.URLEncoder.encode(e.getMessage(), java.nio.charset.StandardCharsets.UTF_8);
        }
    }

    @PostMapping("/estado/{id}")
    @ResponseBody
    public String cambiarEstado(@PathVariable Long id, @RequestBody java.util.Map<String, Boolean> request) {
        try {
            logger.info("Cambiando estado del ganado ID: {}", id);
            Boolean activo = request.get("activo");
            if (activo == null) {
                logger.warn("Parámetro 'activo' no proporcionado");
                return "{\"status\":\"error\",\"message\":\"Parámetro activo requerido\"}";
            }
            ganadoService.cambiarEstadoGanado(id, activo);
            logger.info("✓ Estado cambiado exitosamente. ID: {}, Nuevo estado: {}", id, activo);
            return "{\"status\":\"ok\"}";
        } catch (Exception e) {
            logger.error("✗ Error al cambiar estado: {}", e.getMessage(), e);
            e.printStackTrace();
            return "{\"status\":\"error\",\"message\":\"" + e.getMessage().replace("\"", "\\\"") + "\"}";
        }
    }
    
    @PostMapping("/eliminar/{id}")
    @ResponseBody
    public String eliminar(@PathVariable Long id) {
        try {
            logger.info("=== Eliminando ganado ID: {} ===", id);
            ganadoService.eliminarGanado(id);
            logger.info("✓ Ganado eliminado exitosamente. ID: {}", id);
            return "{\"status\":\"ok\",\"message\":\"Paciente eliminado correctamente\"}";
        } catch (IllegalStateException e) {
            logger.warn("No se puede eliminar: {}", e.getMessage());
            return "{\"status\":\"error\",\"message\":\"" + e.getMessage().replace("\"", "\\\"") + "\"}";
        } catch (Exception e) {
            logger.error("✗ Error al eliminar ganado: {}", e.getMessage(), e);
            return "{\"status\":\"error\",\"message\":\"Error al eliminar paciente: " + e.getMessage().replace("\"", "\\\"") + "\"}";
        }
    }

    @GetMapping(value = "/exportar-pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> exportarPdf() {
        try {
            logger.info("Iniciando exportación de PDF de ganado...");
            List<Ganado> ganado = ganadoService.listarGanado();
            logger.info("Ganado obtenido: {} registros", ganado != null ? ganado.size() : 0);
            
            if (ganado == null || ganado.isEmpty()) {
                logger.warn("No hay ganado para exportar");
                ganado = new java.util.ArrayList<>();
            }
            
            byte[] pdf = PdfExporter.exportarGanadoEstadisticas(ganado);
            logger.info("PDF generado exitosamente, tamaño: {} bytes", pdf.length);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_ganado_estadistico.pdf");
            headers.setContentLength(pdf.length);
            return ResponseEntity.ok().headers(headers).body(pdf);
        } catch (Exception e) {
            logger.error("Error al exportar PDF de ganado: {}", e.getMessage(), e);
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}
