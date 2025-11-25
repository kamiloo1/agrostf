package com.example.agrosoft1.crud.controller;

import com.example.agrosoft1.crud.service.ReportePdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

/**
 * Controlador para generación y descarga de reportes estadísticos en PDF.
 */
@Controller
@RequestMapping("/admin/reportes")
public class ReporteController {
    
    @Autowired
    private ReportePdfService reportePdfService;
    
    /**
     * Muestra la página de reportes
     */
    @GetMapping
    public String mostrarReportes() {
        return "admin/reportes";
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

