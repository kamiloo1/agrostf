package com.example.agrosoft1.crud.util;

import com.example.agrosoft1.crud.entity.Usuario;
import com.example.agrosoft1.crud.entity.Cultivo;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.time.format.DateTimeFormatter;

public class ExcelExporter {

    public static byte[] exportarUsuarios(List<Usuario> usuarios) {
        if (usuarios == null) {
            usuarios = java.util.Collections.emptyList();
        }
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Usuarios");

            String[] headers = {"ID", "Nombre", "Correo", "Número Documento", "Rol"};

            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            font.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(font);
            headerStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (Usuario u : usuarios) {
                Row row = sheet.createRow(rowIdx++);
                if (u.getId() != null) {
                    row.createCell(0).setCellValue(u.getId());
                } else {
                    row.createCell(0).setCellValue("");
                }
                row.createCell(1).setCellValue(u.getNombre() != null ? u.getNombre() : "Sin asignar");
                row.createCell(2).setCellValue(u.getCorreo());
                row.createCell(3).setCellValue(u.getNumeroDocumento() != null ? u.getNumeroDocumento() : "");
                row.createCell(4).setCellValue(u.getRol() != null ? u.getRol() : "Sin asignar");
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al exportar Excel", e);
        }
    }

    public static byte[] exportarCultivos(List<Cultivo> cultivos) {
        if (cultivos == null) {
            cultivos = java.util.Collections.emptyList();
        }
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Cultivos");

            String[] headers = {"ID", "Nombre", "Tipo", "Área (m²)", "Estado", "Fecha Siembra", "Fecha Cosecha", "Activo", "Observaciones"};

            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            font.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(font);
            headerStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            int rowIdx = 1;
            for (Cultivo c : cultivos) {
                Row row = sheet.createRow(rowIdx++);
                
                // ID
                if (c.getIdCultivo() != null) {
                    row.createCell(0).setCellValue(c.getIdCultivo());
                } else {
                    row.createCell(0).setCellValue("");
                }
                
                // Nombre
                row.createCell(1).setCellValue(c.getNombre() != null ? c.getNombre() : "");
                
                // Tipo
                row.createCell(2).setCellValue(c.getTipo() != null ? c.getTipo() : "");
                
                // Área
                row.createCell(3).setCellValue(c.getArea() != null ? c.getArea() : "");
                
                // Estado
                row.createCell(4).setCellValue(c.getEstado() != null ? c.getEstado() : "Activo");
                
                // Fecha Siembra
                if (c.getFechaSiembra() != null) {
                    row.createCell(5).setCellValue(c.getFechaSiembra().format(dateFormatter));
                } else {
                    row.createCell(5).setCellValue("");
                }
                
                // Fecha Cosecha
                if (c.getFechaCosecha() != null) {
                    row.createCell(6).setCellValue(c.getFechaCosecha().format(dateFormatter));
                } else {
                    row.createCell(6).setCellValue("");
                }
                
                // Activo
                String activo = (c.getActivo() != null && c.getActivo()) ? "Sí" : "No";
                row.createCell(7).setCellValue(activo);
                
                // Observaciones
                row.createCell(8).setCellValue(c.getObservaciones() != null ? c.getObservaciones() : "");
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al exportar Excel de cultivos", e);
        }
    }
}



