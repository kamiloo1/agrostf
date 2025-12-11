package com.example.agrosoft1.crud.service;

import com.example.agrosoft1.crud.entity.Ganado;
import com.example.agrosoft1.crud.entity.Tratamiento;
import com.example.agrosoft1.crud.pattern.singleton.ConfiguracionSingleton;
import com.example.agrosoft1.crud.repository.GanadoRepository;
import com.example.agrosoft1.crud.repository.TratamientoRepository;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
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
    
    // PATRÓN SINGLETON: Obtener instancia única de configuración
    private final ConfiguracionSingleton configuracion;
    
    public ReportePdfService() {
        this.configuracion = ConfiguracionSingleton.getInstancia();
    }
    
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
            
            // PATRÓN SINGLETON: Obtener nombre del sistema desde configuración
            String nombreSistema = configuracion.getNombreSistema();
            String versionSistema = configuracion.getVersion();
            
            // Título
            Paragraph titulo = new Paragraph("REPORTE ESTADÍSTICO " + nombreSistema.toUpperCase())
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(titulo);
            
            // Versión del sistema
            if (versionSistema != null) {
                Paragraph version = new Paragraph("Versión " + versionSistema)
                        .setFontSize(10)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(10);
                document.add(version);
            }
            
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
            
            // Gráfica de tratamientos activos vs inactivos
            Paragraph graficaTitulo1 = new Paragraph("GRÁFICA: TRATAMIENTOS ACTIVOS VS INACTIVOS")
                    .setFontSize(14)
                    .setBold()
                    .setMarginTop(20)
                    .setMarginBottom(10)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(graficaTitulo1);
            
            try {
                byte[] graficaTratamientos = generarGraficaPastelTratamientos(tratamientosActivos, tratamientosInactivos);
                if (graficaTratamientos != null && graficaTratamientos.length > 0) {
                    Image imagenTratamientos = new Image(ImageDataFactory.create(graficaTratamientos));
                    imagenTratamientos.setWidth(400);
                    imagenTratamientos.setAutoScaleHeight(true);
                    imagenTratamientos.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);
                    imagenTratamientos.setMarginBottom(20);
                    document.add(imagenTratamientos);
                    logger.info("Gráfica de tratamientos agregada exitosamente al PDF");
                } else {
                    logger.warn("La gráfica de tratamientos está vacía");
                }
            } catch (Exception e) {
                logger.error("Error al generar gráfica de tratamientos: {}", e.getMessage(), e);
                document.add(new Paragraph("Error al generar gráfica de tratamientos: " + e.getMessage())
                        .setFontSize(10)
                        .setFontColor(ColorConstants.RED));
            }
            
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
            
            // Gráfica de pacientes por tipo
            Paragraph graficaTitulo2 = new Paragraph("GRÁFICA: DISTRIBUCIÓN DE PACIENTES POR TIPO")
                    .setFontSize(14)
                    .setBold()
                    .setMarginTop(20)
                    .setMarginBottom(10)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(graficaTitulo2);
            
            try {
                byte[] graficaPacientes = generarGraficaPastelPacientes(pacientesPorTipo);
                if (graficaPacientes != null && graficaPacientes.length > 0) {
                    Image imagenPacientes = new Image(ImageDataFactory.create(graficaPacientes));
                    imagenPacientes.setWidth(400);
                    imagenPacientes.setAutoScaleHeight(true);
                    imagenPacientes.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);
                    imagenPacientes.setMarginBottom(20);
                    document.add(imagenPacientes);
                    logger.info("Gráfica de pacientes agregada exitosamente al PDF");
                } else {
                    logger.warn("La gráfica de pacientes está vacía");
                }
            } catch (Exception e) {
                logger.error("Error al generar gráfica de pacientes: {}", e.getMessage(), e);
                document.add(new Paragraph("Error al generar gráfica de pacientes: " + e.getMessage())
                        .setFontSize(10)
                        .setFontColor(ColorConstants.RED));
            }
            
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
            
            // Gráfica de barras de tratamientos por tipo
            Paragraph graficaTitulo3 = new Paragraph("GRÁFICA: TRATAMIENTOS POR TIPO")
                    .setFontSize(14)
                    .setBold()
                    .setMarginTop(20)
                    .setMarginBottom(10)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(graficaTitulo3);
            
            try {
                byte[] graficaBarras = generarGraficaBarrasTratamientos(tratamientosPorTipo);
                if (graficaBarras != null && graficaBarras.length > 0) {
                    Image imagenBarras = new Image(ImageDataFactory.create(graficaBarras));
                    imagenBarras.setWidth(500);
                    imagenBarras.setAutoScaleHeight(true);
                    imagenBarras.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);
                    imagenBarras.setMarginBottom(20);
                    document.add(imagenBarras);
                    logger.info("Gráfica de barras agregada exitosamente al PDF");
                } else {
                    logger.warn("La gráfica de barras está vacía");
                }
            } catch (Exception e) {
                logger.error("Error al generar gráfica de barras: {}", e.getMessage(), e);
                document.add(new Paragraph("Error al generar gráfica de barras: " + e.getMessage())
                        .setFontSize(10)
                        .setFontColor(ColorConstants.RED));
            }
            
            // Pie de página - PATRÓN SINGLETON: Usar nombre del sistema desde configuración
            Paragraph pie = new Paragraph("Este reporte fue generado automáticamente por el sistema " + nombreSistema)
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
     * Genera una gráfica de pastel para tratamientos activos vs inactivos
     */
    @SuppressWarnings("unchecked")
    private byte[] generarGraficaPastelTratamientos(long activos, long inactivos) throws IOException {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        dataset.setValue("Tratamientos Activos", activos);
        dataset.setValue("Tratamientos Inactivos", inactivos);
        
        JFreeChart chart = ChartFactory.createPieChart(
                "Tratamientos Activos vs Inactivos",
                dataset,
                true,
                true,
                false
        );
        
        PiePlot<String> plot = (PiePlot<String>) chart.getPlot();
        plot.setSectionPaint("Tratamientos Activos", new Color(76, 175, 80));
        plot.setSectionPaint("Tratamientos Inactivos", new Color(244, 67, 54));
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        plot.setNoDataMessage("No hay datos disponibles");
        plot.setCircular(true);
        plot.setLabelGap(0.02);
        
        chart.setTitle(new org.jfree.chart.title.TextTitle("Tratamientos Activos vs Inactivos",
                new Font("SansSerif", Font.BOLD, 16)));
        
        return convertirGraficaABytes(chart, 500, 400);
    }
    
    /**
     * Genera una gráfica de pastel para distribución de pacientes por tipo
     */
    @SuppressWarnings("unchecked")
    private byte[] generarGraficaPastelPacientes(Map<String, Long> pacientesPorTipo) throws IOException {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        for (Map.Entry<String, Long> entry : pacientesPorTipo.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }
        
        JFreeChart chart = ChartFactory.createPieChart(
                "Distribución de Pacientes por Tipo",
                dataset,
                true,
                true,
                false
        );
        
        PiePlot<String> plot = (PiePlot<String>) chart.getPlot();
        // Colores personalizados
        Color[] colores = {
            new Color(33, 150, 243),
            new Color(76, 175, 80),
            new Color(255, 152, 0),
            new Color(156, 39, 176),
            new Color(244, 67, 54),
            new Color(0, 188, 212),
            new Color(255, 87, 34),
            new Color(121, 85, 72)
        };
        
        int colorIndex = 0;
        for (String key : pacientesPorTipo.keySet()) {
            plot.setSectionPaint(key, colores[colorIndex % colores.length]);
            colorIndex++;
        }
        
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 11));
        plot.setNoDataMessage("No hay datos disponibles");
        plot.setCircular(true);
        plot.setLabelGap(0.02);
        
        chart.setTitle(new org.jfree.chart.title.TextTitle("Distribución de Pacientes por Tipo",
                new Font("SansSerif", Font.BOLD, 16)));
        
        return convertirGraficaABytes(chart, 500, 400);
    }
    
    /**
     * Genera una gráfica de barras para tratamientos por tipo
     */
    private byte[] generarGraficaBarrasTratamientos(Map<String, Long> tratamientosPorTipo) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Long> entry : tratamientosPorTipo.entrySet()) {
            dataset.addValue(entry.getValue(), "Cantidad", entry.getKey());
        }
        
        JFreeChart chart = ChartFactory.createBarChart(
                "Tratamientos por Tipo",
                "Tipo de Tratamiento",
                "Cantidad",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );
        
        chart.setTitle(new org.jfree.chart.title.TextTitle("Tratamientos por Tipo",
                new Font("SansSerif", Font.BOLD, 16)));
        
        org.jfree.chart.plot.CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(200, 200, 200));
        plot.setRangeGridlinesVisible(true);
        
        org.jfree.chart.renderer.category.BarRenderer renderer = 
                (org.jfree.chart.renderer.category.BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(33, 150, 243));
        renderer.setDrawBarOutline(false);
        
        return convertirGraficaABytes(chart, 600, 400);
    }
    
    /**
     * Convierte una gráfica JFreeChart a un array de bytes (imagen PNG)
     */
    private byte[] convertirGraficaABytes(JFreeChart chart, int width, int height) throws IOException {
        // Configurar para entorno headless (sin interfaz gráfica)
        System.setProperty("java.awt.headless", "true");
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            // Usar la versión simple de createBufferedImage
            java.awt.image.BufferedImage bufferedImage = chart.createBufferedImage(width, height);
            
            if (bufferedImage == null) {
                throw new IOException("No se pudo crear la imagen de la gráfica");
            }
            
            // Escribir la imagen en formato PNG
            boolean escrito = javax.imageio.ImageIO.write(bufferedImage, "PNG", baos);
            if (!escrito) {
                throw new IOException("No se pudo escribir la imagen en formato PNG");
            }
            
            byte[] resultado = baos.toByteArray();
            if (resultado.length == 0) {
                throw new IOException("La imagen generada está vacía");
            }
            
            logger.debug("Gráfica convertida exitosamente. Tamaño: {} bytes", resultado.length);
            return resultado;
        } catch (Exception e) {
            logger.error("Error al convertir gráfica a bytes: {}", e.getMessage(), e);
            throw new IOException("Error al convertir gráfica a imagen: " + e.getMessage(), e);
        } finally {
            try {
                baos.close();
            } catch (IOException e) {
                logger.warn("Error al cerrar ByteArrayOutputStream: {}", e.getMessage());
            }
        }
    }
    
    /**
     * Guarda el PDF generado en la carpeta "reportes"
     * PATRÓN SINGLETON: Usa la ruta de reportes desde configuración
     */
    private void guardarArchivoEnDisco(byte[] pdfBytes) {
        try {
            // PATRÓN SINGLETON: Obtener ruta de reportes desde configuración
            String rutaReportes = configuracion.getRutaReportes();
            if (rutaReportes == null || rutaReportes.trim().isEmpty()) {
                rutaReportes = "reportes"; // Valor por defecto
            }
            
            File carpetaReportes = new File(rutaReportes);
            if (!carpetaReportes.exists()) {
                carpetaReportes.mkdirs();
                logger.info("Carpeta de reportes creada: {}", carpetaReportes.getAbsolutePath());
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

