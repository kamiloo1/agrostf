package com.example.agrosoft1.crud.util;

import com.example.agrosoft1.crud.entity.Usuario;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class ExcelExporter {

    public static byte[] exportarUsuarios(List<Usuario> usuarios) {
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
}



