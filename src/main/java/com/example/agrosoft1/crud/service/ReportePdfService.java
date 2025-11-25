package com.example.agrosoft1.crud.service;

import com.example.agrosoft1.crud.entity.Ganado;
import com.example.agrosoft1.crud.entity.Tratamiento;
import com.example.agrosoft1.crud.repository.GanadoRepository;
import com.example.agrosoft1.crud.repository.TratamientoRepository;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servicio para generación de reportes estadísticos en PDF.
 * 
 * Genera PDFs con estadísticas del sistema incluyendo:
 * - Total de pacientes
 * - Total de tratamientos
 * - Tratamientos activos e inactivos
 * - Gráficas o tablas de datos
 */
@Service
public class ReportePdfService {
    
    private static final Logger logger = LoggerFactory.getLogger(ReportePdfService.class);
    
    @Autowired
    private GanadoRepository ganadoRepository;
    
    @Autowired
    private TratamientoRepository tratamientoRepository;
    
    /**
     * Genera un reporte estadístico completo en PDF
     * 
     * @return Array de bytes con el contenido del PDF
     */
    public byte[] generarReporteEstadistico() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        
        try {
            // Obtener estadísticas
            long totalPacientes = ganadoRepository.count();
            long totalTratamientos = tratamientoRepository.count();
            List<Tratamiento> tratamientos = tratamientoRepository.findAll();
            List<Ganado> pacientes = ganadoRepository.findAll();
            
            // Calcular tratamientos activos (últimos 30 días)
            LocalDate fechaLimite = LocalDate.now().minusDays(30);
            long tratamientosActivos = tratamientos.stream()
                    .filter(t -> t.getFechaTratamiento() != null && 
                               t.getFechaTratamiento().isAfter(fechaLimite))
                    .count();
            long tratamientosInactivos = totalTratamientos - tratamientosActivos;
            
            // Título
            Paragraph titulo = new Paragraph("REPORTE ESTADÍSTICO AGROSOFT")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(titulo);
            
            // Fecha de generación
            Paragraph fecha = new Paragraph("Fecha de generación: " + 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(30);
            document.add(fecha);
            
            // Estadísticas principales
            Paragraph seccion1 = new Paragraph("ESTADÍSTICAS GENERALES")
                    .setFontSize(16)
                    .setBold()
                    .setMarginTop(20)
                    .setMarginBottom(10);
            document.add(seccion1);
            
            // Tabla de estadísticas
            Table tablaEstadisticas = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                    .useAllAvailableWidth()
                    .setMarginBottom(20);
            
            tablaEstadisticas.addHeaderCell(new Paragraph("Indicador").setBold().setBackgroundColor(ColorConstants.LIGHT_GRAY));
            tablaEstadisticas.addHeaderCell(new Paragraph("Valor").setBold().setBackgroundColor(ColorConstants.LIGHT_GRAY));
            
            tablaEstadisticas.addCell(new Paragraph("Total de Pacientes"));
            tablaEstadisticas.addCell(new Paragraph(String.valueOf(totalPacientes)));
            
            tablaEstadisticas.addCell(new Paragraph("Total de Tratamientos"));
            tablaEstadisticas.addCell(new Paragraph(String.valueOf(totalTratamientos)));
            
            tablaEstadisticas.addCell(new Paragraph("Tratamientos Activos (últimos 30 días)"));
            tablaEstadisticas.addCell(new Paragraph(String.valueOf(tratamientosActivos)));
            
            tablaEstadisticas.addCell(new Paragraph("Tratamientos Inactivos"));
            tablaEstadisticas.addCell(new Paragraph(String.valueOf(tratamientosInactivos)));
            
            document.add(tablaEstadisticas);
            
            // Estadísticas por tipo de paciente
            Paragraph seccion2 = new Paragraph("PACIENTES POR TIPO")
                    .setFontSize(16)
                    .setBold()
                    .setMarginTop(20)
                    .setMarginBottom(10);
            document.add(seccion2);
            
            Map<String, Long> pacientesPorTipo = pacientes.stream()
                    .collect(Collectors.groupingBy(
                            p -> p.getTipo() != null ? p.getTipo() : "Sin tipo",
                            Collectors.counting()));
            
            Table tablaTipos = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                    .useAllAvailableWidth()
                    .setMarginBottom(20);
            
            tablaTipos.addHeaderCell(new Paragraph("Tipo de Animal").setBold().setBackgroundColor(ColorConstants.LIGHT_GRAY));
            tablaTipos.addHeaderCell(new Paragraph("Cantidad").setBold().setBackgroundColor(ColorConstants.LIGHT_GRAY));
            
            for (Map.Entry<String, Long> entry : pacientesPorTipo.entrySet()) {
                tablaTipos.addCell(new Paragraph(entry.getKey()));
                tablaTipos.addCell(new Paragraph(String.valueOf(entry.getValue())));
            }
            
            document.add(tablaTipos);
            
            // Estadísticas por tipo de tratamiento
            Paragraph seccion3 = new Paragraph("TRATAMIENTOS POR TIPO")
                    .setFontSize(16)
                    .setBold()
                    .setMarginTop(20)
                    .setMarginBottom(10);
            document.add(seccion3);
            
            Map<String, Long> tratamientosPorTipo = tratamientos.stream()
                    .collect(Collectors.groupingBy(
                            t -> t.getTipoTratamiento() != null ? t.getTipoTratamiento() : "Sin tipo",
                            Collectors.counting()));
            
            Table tablaTratamientos = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                    .useAllAvailableWidth()
                    .setMarginBottom(20);
            
            tablaTratamientos.addHeaderCell(new Paragraph("Tipo de Tratamiento").setBold().setBackgroundColor(ColorConstants.LIGHT_GRAY));
            tablaTratamientos.addHeaderCell(new Paragraph("Cantidad").setBold().setBackgroundColor(ColorConstants.LIGHT_GRAY));
            
            for (Map.Entry<String, Long> entry : tratamientosPorTipo.entrySet()) {
                tablaTratamientos.addCell(new Paragraph(entry.getKey()));
                tablaTratamientos.addCell(new Paragraph(String.valueOf(entry.getValue())));
            }
            
            document.add(tablaTratamientos);
            
            // Pie de página
            Paragraph pie = new Paragraph("Este reporte fue generado automáticamente por el sistema AgroSoft")
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(30);
            document.add(pie);
            
        } finally {
            document.close();
        }
        
        byte[] pdfBytes = baos.toByteArray();
        
        // Guardar archivo en carpeta "reportes"
        guardarArchivoEnDisco(pdfBytes);
        
        return pdfBytes;
    }
    
    /**
     * Guarda el PDF generado en la carpeta "reportes"
     */
    private void guardarArchivoEnDisco(byte[] pdfBytes) {
        try {
            File carpetaReportes = new File("reportes");
            if (!carpetaReportes.exists()) {
                carpetaReportes.mkdirs();
            }
            
            String nombreArchivo = "reporte_estadistico_" + 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";
            File archivo = new File(carpetaReportes, nombreArchivo);
            
            try (FileOutputStream fos = new FileOutputStream(archivo)) {
                fos.write(pdfBytes);
            }
            
            logger.info("Reporte PDF guardado en: {}", archivo.getAbsolutePath());
        } catch (IOException e) {
            logger.error("Error al guardar reporte en disco: {}", e.getMessage());
        }
    }
}

