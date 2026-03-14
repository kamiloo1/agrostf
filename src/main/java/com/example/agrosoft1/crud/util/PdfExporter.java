package com.example.agrosoft1.crud.util;

import com.example.agrosoft1.crud.entity.Cultivo;
import com.example.agrosoft1.crud.entity.Ganado;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class PdfExporter {

    private static final DeviceRgb COLOR_VERDE_OSCURO = new DeviceRgb(13, 79, 61);
    private static final DeviceRgb COLOR_VERDE_MED = new DeviceRgb(26, 122, 94);
    private static final DeviceRgb COLOR_VERDE_ACENTO = new DeviceRgb(45, 134, 89);
    private static final DeviceRgb COLOR_GRIS_CLARO = new DeviceRgb(245, 247, 250);

    public static byte[] exportarCultivosEstadisticas(List<Cultivo> cultivos) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // Configurar página
            document.setMargins(50, 50, 50, 50);

            // Título principal
            Paragraph titulo = new Paragraph("REPORTE ESTADÍSTICO DE CULTIVOS")
                    .setFontSize(24)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(COLOR_VERDE_OSCURO)
                    .setMarginBottom(10);
            document.add(titulo);

            // Subtítulo con fecha
            String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            Paragraph subtitulo = new Paragraph("AgroSoft - Generado el " + fecha)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.GRAY)
                    .setMarginBottom(30);
            document.add(subtitulo);

            // Estadísticas generales
            document.add(crearSeccionEstadisticas(cultivos));

            // Diagrama de tipos de cultivos
            document.add(crearSeccionTipos(cultivos));

            // Tabla de cultivos
            document.add(crearTablaCultivos(cultivos));

            // Análisis y recomendaciones
            document.add(crearSeccionAnalisis(cultivos));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al exportar PDF", e);
        }
    }

    private static Paragraph crearSeccionEstadisticas(List<Cultivo> cultivos) {
        Paragraph seccion = new Paragraph("ESTADÍSTICAS GENERALES")
                .setFontSize(18)
                .setBold()
                .setFontColor(COLOR_VERDE_MED)
                .setMarginTop(20)
                .setMarginBottom(15);

        long totalCultivos = cultivos.size();
        long activos = cultivos.stream().filter(c -> c.getActivo() != null && c.getActivo()).count();
        long inactivos = totalCultivos - activos;
        
        // Calcular área total (asumiendo que area es String con formato "123 m²")
        double areaTotal = cultivos.stream()
                .filter(c -> c.getArea() != null && !c.getArea().isEmpty())
                .mapToDouble(c -> {
                    try {
                        String areaStr = c.getArea().replaceAll("[^0-9.]", "");
                        return areaStr.isEmpty() ? 0 : Double.parseDouble(areaStr);
                    } catch (Exception e) {
                        return 0;
                    }
                })
                .sum();

        // Contar tipos únicos
        long tiposUnicos = cultivos.stream()
                .filter(c -> c.getTipo() != null && !c.getTipo().isEmpty())
                .map(Cultivo::getTipo)
                .distinct()
                .count();

        // Crear tabla de estadísticas
        Table tablaStats = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(20);

        agregarCeldaEstadistica(tablaStats, "Total de Cultivos", String.valueOf(totalCultivos));
        agregarCeldaEstadistica(tablaStats, "Cultivos Activos", String.valueOf(activos));
        agregarCeldaEstadistica(tablaStats, "Cultivos Inactivos", String.valueOf(inactivos));
        agregarCeldaEstadistica(tablaStats, "Área Total Monitoreada", String.format("%.2f m²", areaTotal));
        agregarCeldaEstadistica(tablaStats, "Tipos Diferentes", String.valueOf(tiposUnicos));
        agregarCeldaEstadistica(tablaStats, "Área Promedio", totalCultivos > 0 ? 
                String.format("%.2f m²", areaTotal / totalCultivos) : "0 m²");

        Paragraph resultado = new Paragraph();
        resultado.add(seccion);
        resultado.add(tablaStats);
        return resultado;
    }

    private static void agregarCeldaEstadistica(Table tabla, String label, String valor) {
        Cell celdaLabel = new Cell()
                .add(new Paragraph(label).setFontSize(11).setBold())
                .setBackgroundColor(COLOR_GRIS_CLARO)
                .setPadding(10);
        
        Cell celdaValor = new Cell()
                .add(new Paragraph(valor).setFontSize(12))
                .setPadding(10);
        
        tabla.addCell(celdaLabel);
        tabla.addCell(celdaValor);
    }

    private static Paragraph crearSeccionTipos(List<Cultivo> cultivos) {
        Paragraph seccion = new Paragraph("DISTRIBUCIÓN POR TIPO DE CULTIVO")
                .setFontSize(18)
                .setBold()
                .setFontColor(COLOR_VERDE_MED)
                .setMarginTop(20)
                .setMarginBottom(15);

        // Agrupar por tipo
        Map<String, Long> tiposCount = cultivos.stream()
                .filter(c -> c.getTipo() != null && !c.getTipo().isEmpty())
                .collect(Collectors.groupingBy(
                        c -> c.getTipo().equals("null") ? "Sin tipo" : c.getTipo(),
                        Collectors.counting()
                ));

        if (tiposCount.isEmpty()) {
            tiposCount.put("Sin tipo", (long) cultivos.size());
        }

        // Crear tabla de distribución
        Table tablaTipos = new Table(UnitValue.createPercentArray(new float[]{2, 1, 1}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(20);

        // Encabezados
        Cell header1 = new Cell().add(new Paragraph("Tipo").setBold())
                .setBackgroundColor(COLOR_VERDE_ACENTO)
                .setFontColor(ColorConstants.WHITE)
                .setPadding(10);
        Cell header2 = new Cell().add(new Paragraph("Cantidad").setBold())
                .setBackgroundColor(COLOR_VERDE_ACENTO)
                .setFontColor(ColorConstants.WHITE)
                .setPadding(10);
        Cell header3 = new Cell().add(new Paragraph("Porcentaje").setBold())
                .setBackgroundColor(COLOR_VERDE_ACENTO)
                .setFontColor(ColorConstants.WHITE)
                .setPadding(10);
        
        tablaTipos.addHeaderCell(header1);
        tablaTipos.addHeaderCell(header2);
        tablaTipos.addHeaderCell(header3);

        long total = cultivos.size();
        for (Map.Entry<String, Long> entry : tiposCount.entrySet()) {
            long cantidad = entry.getValue();
            double porcentaje = total > 0 ? (cantidad * 100.0 / total) : 0;
            
            tablaTipos.addCell(new Cell().add(new Paragraph(entry.getKey())).setPadding(8));
            tablaTipos.addCell(new Cell().add(new Paragraph(String.valueOf(cantidad))).setPadding(8));
            tablaTipos.addCell(new Cell().add(new Paragraph(String.format("%.1f%%", porcentaje))).setPadding(8));
        }

        Paragraph resultado = new Paragraph();
        resultado.add(seccion);
        resultado.add(tablaTipos);
        return resultado;
    }

    private static Paragraph crearTablaCultivos(List<Cultivo> cultivos) {
        Paragraph seccion = new Paragraph("DETALLE DE CULTIVOS")
                .setFontSize(18)
                .setBold()
                .setFontColor(COLOR_VERDE_MED)
                .setMarginTop(20)
                .setMarginBottom(15);

        Table tabla = new Table(UnitValue.createPercentArray(new float[]{1, 2, 2, 2, 1}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(20);

        // Encabezados
        String[] headers = {"ID", "Nombre", "Tipo", "Área", "Estado"};
        for (String header : headers) {
            Cell cell = new Cell().add(new Paragraph(header).setBold())
                    .setBackgroundColor(COLOR_VERDE_ACENTO)
                    .setFontColor(ColorConstants.WHITE)
                    .setPadding(8);
            tabla.addHeaderCell(cell);
        }

        // Datos
        for (Cultivo c : cultivos) {
            tabla.addCell(new Cell().add(new Paragraph(c.getId() != null ? c.getId().toString() : "")).setPadding(6));
            tabla.addCell(new Cell().add(new Paragraph(c.getNombre() != null ? c.getNombre() : "")).setPadding(6));
            tabla.addCell(new Cell().add(new Paragraph(c.getTipo() != null && !c.getTipo().equals("null") ? c.getTipo() : "Sin tipo")).setPadding(6));
            tabla.addCell(new Cell().add(new Paragraph(c.getArea() != null ? c.getArea() : "0 m²")).setPadding(6));
            
            String estado = (c.getActivo() != null && c.getActivo()) ? "Activo" : "Inactivo";
            com.itextpdf.kernel.colors.Color colorEstado = (c.getActivo() != null && c.getActivo()) ? COLOR_VERDE_ACENTO : ColorConstants.RED;
            Cell cellEstado = new Cell().add(new Paragraph(estado))
                    .setPadding(6)
                    .setFontColor(colorEstado);
            tabla.addCell(cellEstado);
        }

        Paragraph resultado = new Paragraph();
        resultado.add(seccion);
        resultado.add(tabla);
        return resultado;
    }

    private static Paragraph crearSeccionAnalisis(List<Cultivo> cultivos) {
        Paragraph seccion = new Paragraph("ANÁLISIS Y RECOMENDACIONES")
                .setFontSize(18)
                .setBold()
                .setFontColor(COLOR_VERDE_MED)
                .setMarginTop(20)
                .setMarginBottom(15);

        long activos = cultivos.stream().filter(c -> c.getActivo() != null && c.getActivo()).count();
        long total = cultivos.size();
        double porcentajeActivos = total > 0 ? (activos * 100.0 / total) : 0;

        List<String> recomendaciones = new ArrayList<>();
        
        if (porcentajeActivos < 50) {
            recomendaciones.add("• Menos del 50% de los cultivos están activos. Considera revisar y reactivar cultivos inactivos.");
        }
        
        if (total < 5) {
            recomendaciones.add("• Tienes pocos cultivos registrados. Considera diversificar la producción.");
        }

        long tiposUnicos = cultivos.stream()
                .filter(c -> c.getTipo() != null && !c.getTipo().isEmpty() && !c.getTipo().equals("null"))
                .map(Cultivo::getTipo)
                .distinct()
                .count();
        
        if (tiposUnicos < 2 && total > 3) {
            recomendaciones.add("• Poca diversidad de tipos de cultivos. Considera diversificar para mejorar la producción.");
        }

        if (recomendaciones.isEmpty()) {
            recomendaciones.add("• Excelente gestión de cultivos. Mantén el seguimiento regular.");
            recomendaciones.add("• Continúa monitoreando el estado de tus cultivos activos.");
        }

        Paragraph analisis = new Paragraph();
        analisis.add(seccion);
        
        for (String rec : recomendaciones) {
            analisis.add(new Paragraph(rec).setMarginBottom(5).setFontSize(11));
        }

        return analisis;
    }

    // ============================================
    // MÉTODOS PARA EXPORTAR GANADO
    // ============================================

    public static byte[] exportarGanadoEstadisticas(List<Ganado> ganado) {
        if (ganado == null) {
            ganado = new java.util.ArrayList<>();
        }
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // Configurar página
            document.setMargins(50, 50, 50, 50);

            // Título principal
            Paragraph titulo = new Paragraph("REPORTE ESTADÍSTICO DE GANADO")
                    .setFontSize(24)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(COLOR_VERDE_OSCURO)
                    .setMarginBottom(10);
            document.add(titulo);

            // Subtítulo con fecha
            String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            Paragraph subtitulo = new Paragraph("AgroSoft - Generado el " + fecha)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.GRAY)
                    .setMarginBottom(30);
            document.add(subtitulo);

            // Validar que hay datos
            if (ganado.isEmpty()) {
                Paragraph mensaje = new Paragraph("No hay datos de ganado para mostrar en este reporte.")
                        .setFontSize(14)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginTop(50);
                document.add(mensaje);
            } else {
                // Estadísticas generales
                document.add(crearSeccionEstadisticasGanado(ganado));

                // Diagrama de tipos de ganado
                document.add(crearSeccionTiposGanado(ganado));

                // Tabla de ganado
                document.add(crearTablaGanado(ganado));

                // Análisis y recomendaciones
                document.add(crearSeccionAnalisisGanado(ganado));
            }

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al exportar PDF de ganado: " + e.getMessage(), e);
        }
    }

    private static Paragraph crearSeccionEstadisticasGanado(List<Ganado> ganado) {
        if (ganado == null) {
            ganado = new java.util.ArrayList<>();
        }
        
        Paragraph seccion = new Paragraph("ESTADÍSTICAS GENERALES")
                .setFontSize(18)
                .setBold()
                .setFontColor(COLOR_VERDE_MED)
                .setMarginTop(20)
                .setMarginBottom(15);

        long totalGanado = ganado.size();
        long activos = ganado.stream()
                .filter(g -> g != null && g.getActivo() != null && g.getActivo())
                .count();
        long inactivos = totalGanado - activos;
        long saludables = ganado.stream()
                .filter(g -> g != null && "Saludable".equals(g.getEstadoSalud()))
                .count();
        long enObservacion = ganado.stream()
                .filter(g -> g != null && "En observación".equals(g.getEstadoSalud()))
                .count();
        long enfermos = ganado.stream()
                .filter(g -> g != null && "Enfermo".equals(g.getEstadoSalud()))
                .count();
        
        double pesoPromedio = ganado.stream()
                .filter(g -> g != null && g.getPeso() != null)
                .mapToDouble(Ganado::getPeso)
                .average()
                .orElse(0.0);

        double pesoTotal = ganado.stream()
                .filter(g -> g != null && g.getPeso() != null)
                .mapToDouble(Ganado::getPeso)
                .sum();

        long tiposUnicos = ganado.stream()
                .filter(g -> g != null && g.getTipo() != null && !g.getTipo().isEmpty())
                .map(Ganado::getTipo)
                .distinct()
                .count();

        // Crear tabla de estadísticas
        Table tablaStats = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(20);

        agregarCeldaEstadistica(tablaStats, "Total de Animales", String.valueOf(totalGanado));
        agregarCeldaEstadistica(tablaStats, "Animales Activos", String.valueOf(activos));
        agregarCeldaEstadistica(tablaStats, "Animales Inactivos", String.valueOf(inactivos));
        agregarCeldaEstadistica(tablaStats, "Saludables", String.valueOf(saludables));
        agregarCeldaEstadistica(tablaStats, "En Observación", String.valueOf(enObservacion));
        agregarCeldaEstadistica(tablaStats, "Enfermos", String.valueOf(enfermos));
        agregarCeldaEstadistica(tablaStats, "Peso Promedio", String.format("%.2f kg", pesoPromedio));
        agregarCeldaEstadistica(tablaStats, "Peso Total", String.format("%.2f kg", pesoTotal));
        agregarCeldaEstadistica(tablaStats, "Tipos Diferentes", String.valueOf(tiposUnicos));

        Paragraph resultado = new Paragraph();
        resultado.add(seccion);
        resultado.add(tablaStats);
        return resultado;
    }

    private static Paragraph crearSeccionTiposGanado(List<Ganado> ganado) {
        if (ganado == null) {
            ganado = new java.util.ArrayList<>();
        }
        
        Paragraph seccion = new Paragraph("DISTRIBUCIÓN POR TIPO DE ANIMAL")
                .setFontSize(18)
                .setBold()
                .setFontColor(COLOR_VERDE_MED)
                .setMarginTop(20)
                .setMarginBottom(15);

        // Agrupar por tipo
        Map<String, Long> tiposCount = ganado.stream()
                .filter(g -> g != null && g.getTipo() != null && !g.getTipo().isEmpty())
                .collect(Collectors.groupingBy(
                        g -> g.getTipo(),
                        Collectors.counting()
                ));

        if (tiposCount.isEmpty()) {
            tiposCount.put("Sin tipo", (long) ganado.size());
        }

        // Crear tabla de distribución
        Table tablaTipos = new Table(UnitValue.createPercentArray(new float[]{2, 1, 1}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(20);

        // Encabezados
        Cell header1 = new Cell().add(new Paragraph("Tipo").setBold())
                .setBackgroundColor(COLOR_VERDE_ACENTO)
                .setFontColor(ColorConstants.WHITE)
                .setPadding(10);
        Cell header2 = new Cell().add(new Paragraph("Cantidad").setBold())
                .setBackgroundColor(COLOR_VERDE_ACENTO)
                .setFontColor(ColorConstants.WHITE)
                .setPadding(10);
        Cell header3 = new Cell().add(new Paragraph("Porcentaje").setBold())
                .setBackgroundColor(COLOR_VERDE_ACENTO)
                .setFontColor(ColorConstants.WHITE)
                .setPadding(10);
        
        tablaTipos.addHeaderCell(header1);
        tablaTipos.addHeaderCell(header2);
        tablaTipos.addHeaderCell(header3);

        long total = ganado.size();
        for (Map.Entry<String, Long> entry : tiposCount.entrySet()) {
            long cantidad = entry.getValue();
            double porcentaje = total > 0 ? (cantidad * 100.0 / total) : 0;
            
            tablaTipos.addCell(new Cell().add(new Paragraph(entry.getKey())).setPadding(8));
            tablaTipos.addCell(new Cell().add(new Paragraph(String.valueOf(cantidad))).setPadding(8));
            tablaTipos.addCell(new Cell().add(new Paragraph(String.format("%.1f%%", porcentaje))).setPadding(8));
        }

        Paragraph resultado = new Paragraph();
        resultado.add(seccion);
        resultado.add(tablaTipos);
        return resultado;
    }

    private static Paragraph crearTablaGanado(List<Ganado> ganado) {
        if (ganado == null) {
            ganado = new java.util.ArrayList<>();
        }
        
        Paragraph seccion = new Paragraph("DETALLE DE GANADO")
                .setFontSize(18)
                .setBold()
                .setFontColor(COLOR_VERDE_MED)
                .setMarginTop(20)
                .setMarginBottom(15);

        Table tabla = new Table(UnitValue.createPercentArray(new float[]{1, 2, 2, 1, 1, 2, 1}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(20);

        // Encabezados
        String[] headers = {"ID", "Tipo", "Raza", "Edad", "Peso", "Estado Salud", "Estado"};
        for (String header : headers) {
            Cell cell = new Cell().add(new Paragraph(header).setBold())
                    .setBackgroundColor(COLOR_VERDE_ACENTO)
                    .setFontColor(ColorConstants.WHITE)
                    .setPadding(8);
            tabla.addHeaderCell(cell);
        }

        // Datos
        if (ganado.isEmpty()) {
            // Agregar una fila indicando que no hay datos
            Cell cellVacio = new Cell(1, headers.length)
                    .add(new Paragraph("No hay registros de ganado disponibles"))
                    .setPadding(10)
                    .setTextAlignment(TextAlignment.CENTER);
            tabla.addCell(cellVacio);
        } else {
            for (Ganado g : ganado) {
                if (g == null) continue; // Saltar elementos null
                
                tabla.addCell(new Cell().add(new Paragraph(g.getIdGanado() != null ? g.getIdGanado().toString() : "")).setPadding(6));
                tabla.addCell(new Cell().add(new Paragraph(g.getTipo() != null ? g.getTipo() : "")).setPadding(6));
                tabla.addCell(new Cell().add(new Paragraph(g.getRaza() != null ? g.getRaza() : "—")).setPadding(6));
                tabla.addCell(new Cell().add(new Paragraph(g.getEdad() != null ? g.getEdad() + " meses" : "—")).setPadding(6));
                tabla.addCell(new Cell().add(new Paragraph(g.getPeso() != null ? String.format("%.2f kg", g.getPeso()) : "—")).setPadding(6));
                
                String estadoSalud = g.getEstadoSalud() != null ? g.getEstadoSalud() : "No definido";
                DeviceRgb colorSalud;
                if ("Saludable".equals(estadoSalud)) {
                    colorSalud = COLOR_VERDE_ACENTO;
                } else if ("En observación".equals(estadoSalud)) {
                    colorSalud = new DeviceRgb(255, 193, 7);
                } else {
                    colorSalud = new DeviceRgb(220, 53, 69); // Rojo
                }
                Cell cellSalud = new Cell().add(new Paragraph(estadoSalud))
                        .setPadding(6)
                        .setFontColor(colorSalud);
                tabla.addCell(cellSalud);
                
                String estado = (g.getActivo() != null && g.getActivo()) ? "Activo" : "Inactivo";
                DeviceRgb colorEstado = (g.getActivo() != null && g.getActivo()) ? COLOR_VERDE_ACENTO : new DeviceRgb(220, 53, 69);
                Cell cellEstado = new Cell().add(new Paragraph(estado))
                        .setPadding(6)
                        .setFontColor(colorEstado);
                tabla.addCell(cellEstado);
            }
        }

        Paragraph resultado = new Paragraph();
        resultado.add(seccion);
        resultado.add(tabla);
        return resultado;
    }

    private static Paragraph crearSeccionAnalisisGanado(List<Ganado> ganado) {
        if (ganado == null) {
            ganado = new java.util.ArrayList<>();
        }
        
        Paragraph seccion = new Paragraph("ANÁLISIS Y RECOMENDACIONES")
                .setFontSize(18)
                .setBold()
                .setFontColor(COLOR_VERDE_MED)
                .setMarginTop(20)
                .setMarginBottom(15);

        long activos = ganado.stream()
                .filter(g -> g != null && g.getActivo() != null && g.getActivo())
                .count();
        long total = ganado.size();
        double porcentajeActivos = total > 0 ? (activos * 100.0 / total) : 0;
        long enfermos = ganado.stream()
                .filter(g -> g != null && "Enfermo".equals(g.getEstadoSalud()))
                .count();
        long enObservacion = ganado.stream()
                .filter(g -> g != null && "En observación".equals(g.getEstadoSalud()))
                .count();

        List<String> recomendaciones = new ArrayList<>();
        
        if (porcentajeActivos < 50) {
            recomendaciones.add("• Menos del 50% de los animales están activos. Considera revisar y reactivar animales inactivos.");
        }
        
        if (enfermos > total * 0.1) {
            recomendaciones.add("• Más del 10% de los animales están enfermos. Se recomienda revisión veterinaria urgente.");
        }
        
        if (enObservacion > total * 0.2) {
            recomendaciones.add("• Más del 20% de los animales están en observación. Monitoreo intensivo recomendado.");
        }
        
        if (total < 5) {
            recomendaciones.add("• Tienes pocos animales registrados. Considera expandir el inventario.");
        }

        long tiposUnicos = ganado.stream()
                .filter(g -> g != null && g.getTipo() != null && !g.getTipo().isEmpty())
                .map(Ganado::getTipo)
                .distinct()
                .count();
        
        if (tiposUnicos < 2 && total > 3) {
            recomendaciones.add("• Poca diversidad de tipos de animales. Considera diversificar para mejorar la producción.");
        }

        if (recomendaciones.isEmpty()) {
            recomendaciones.add("• Excelente gestión de ganado. Mantén el seguimiento regular.");
            recomendaciones.add("• Continúa monitoreando el estado de salud de tus animales activos.");
        }

        Paragraph analisis = new Paragraph();
        analisis.add(seccion);
        
        for (String rec : recomendaciones) {
            analisis.add(new Paragraph(rec).setMarginBottom(5).setFontSize(11));
        }

        return analisis;
    }
}

