package com.example.agrosoft1.crud.controller;

import com.example.agrosoft1.crud.pattern.factory.ReporteFactory;
import com.example.agrosoft1.crud.pattern.factory.reporte.ReporteService;
import com.example.agrosoft1.crud.service.ReportePdfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

/**
 * Controlador para generación y descarga de reportes estadísticos en PDF.
 */
@Controller
@RequestMapping("/admin/reportes")
public class ReporteController {
    
    private static final Logger logger = LoggerFactory.getLogger(ReporteController.class);
    
    @Autowired
    private ReportePdfService reportePdfService;
    
    /**
     * Muestra la página de reportes
     * PATRÓN FACTORY: Muestra información de los tipos de reportes disponibles
     */
    @GetMapping
    public String mostrarReportes(Model model) {
        // PATRÓN FACTORY: Obtener información de los tipos de reportes disponibles
        try {
            ReporteService reporteCultivo = ReporteFactory.crearReporte(ReporteFactory.TipoReporte.CULTIVO);
            ReporteService reporteGanado = ReporteFactory.crearReporte(ReporteFactory.TipoReporte.GANADO);
            ReporteService reporteGeneral = ReporteFactory.crearReporte(ReporteFactory.TipoReporte.GENERAL);
            
            model.addAttribute("reporteCultivo", reporteCultivo);
            model.addAttribute("reporteGanado", reporteGanado);
            model.addAttribute("reporteGeneral", reporteGeneral);
            
            logger.info("Página de reportes cargada con información de Factory");
        } catch (Exception e) {
            logger.error("Error al cargar información de reportes: {}", e.getMessage());
        }
        
        return "admin/reportes";
    }
    
    /**
     * PATRÓN FACTORY: Genera un reporte según el tipo especificado
     * 
     * @param tipo Tipo de reporte (CULTIVO, GANADO, GENERAL)
     * @param model Modelo para pasar datos a la vista
     * @return Vista con los datos del reporte
     */
    @GetMapping("/generar")
    public String generarReporte(@RequestParam(required = false, defaultValue = "GENERAL") String tipo, Model model) {
        try {
            // PATRÓN FACTORY: Crear el tipo de reporte solicitado
            ReporteService reporteService = ReporteFactory.crearReporte(tipo);
            
            // Generar el reporte
            var datosReporte = reporteService.generarReporte();
            
            model.addAttribute("reporte", datosReporte);
            model.addAttribute("nombreReporte", reporteService.getNombreReporte());
            model.addAttribute("descripcion", reporteService.getDescripcion());
            model.addAttribute("tipoReporte", tipo);
            
            logger.info("Reporte {} generado exitosamente usando Factory", tipo);
            
            return "admin/reporte_detalle";
        } catch (IllegalArgumentException e) {
            logger.error("Tipo de reporte no válido: {}", tipo);
            model.addAttribute("error", "Tipo de reporte no válido: " + tipo);
            return "admin/reportes";
        } catch (Exception e) {
            logger.error("Error al generar reporte: {}", e.getMessage(), e);
            model.addAttribute("error", "Error al generar el reporte: " + e.getMessage());
            return "admin/reportes";
        }
    }
    
    /**
     * Genera y descarga el reporte estadístico en PDF
     * 
     * @return ResponseEntity con el archivo PDF
     */
    @GetMapping("/generar-pdf")
    public ResponseEntity<byte[]> generarReportePdf() {
        try {
            byte[] pdfBytes = reportePdfService.generarReporteEstadistico();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", 
                "reporte_estadistico_" + System.currentTimeMillis() + ".pdf");
            headers.setContentLength(pdfBytes.length);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

