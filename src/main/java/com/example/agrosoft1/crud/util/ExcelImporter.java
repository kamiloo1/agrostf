package com.example.agrosoft1.crud.util;

import com.example.agrosoft1.crud.entity.*;
import com.example.agrosoft1.crud.repository.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio para importar datos desde archivos Excel
 */
@Component
public class ExcelImporter {
    
    private static final Logger logger = LoggerFactory.getLogger(ExcelImporter.class);
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private GanadoRepository ganadoRepository;
    
    @Autowired
    private TratamientoRepository tratamientoRepository;
    
    @Autowired
    private CultivoRepository cultivoRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Importa datos desde un archivo Excel
     * El Excel debe tener hojas separadas: Usuarios, Ganado, Tratamientos, Cultivos
     */
    public Map<String, Object> importarDatos(MultipartFile file) {
        Map<String, Object> resultado = new HashMap<>();
        List<String> errores = new ArrayList<>();
        int usuariosCreados = 0;
        int ganadoCreado = 0;
        int tratamientosCreados = 0;
        int cultivosCreados = 0;

        if (file == null || file.isEmpty()) {
            resultado.put("status", "error");
            resultado.put("mensaje", "No se ha seleccionado ningún archivo o el archivo está vacío");
            resultado.put("errores", errores);
            return resultado;
        }

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            
            logger.info("Iniciando importación de datos desde Excel: {}", file.getOriginalFilename());
            
            // Importar Usuarios
            Sheet usuariosSheet = workbook.getSheet("Usuarios");
            if (usuariosSheet != null) {
                try {
                    usuariosCreados = importarUsuarios(usuariosSheet);
                    logger.info("Usuarios importados: {}", usuariosCreados);
                } catch (Exception e) {
                    logger.error("Error al importar usuarios: {}", e.getMessage());
                    errores.add("Error en hoja Usuarios: " + e.getMessage());
                }
            }
            
            // Importar Ganado
            Sheet ganadoSheet = workbook.getSheet("Ganado");
            if (ganadoSheet != null) {
                try {
                    ganadoCreado = importarGanado(ganadoSheet);
                    logger.info("Ganado importado: {}", ganadoCreado);
                } catch (Exception e) {
                    logger.error("Error al importar ganado: {}", e.getMessage());
                    errores.add("Error en hoja Ganado: " + e.getMessage());
                }
            }
            
            // Importar Tratamientos
            Sheet tratamientosSheet = workbook.getSheet("Tratamientos");
            if (tratamientosSheet != null) {
                try {
                    tratamientosCreados = importarTratamientos(tratamientosSheet);
                    logger.info("Tratamientos importados: {}", tratamientosCreados);
                } catch (Exception e) {
                    logger.error("Error al importar tratamientos: {}", e.getMessage());
                    errores.add("Error en hoja Tratamientos: " + e.getMessage());
                }
            }
            
            // Importar Cultivos
            Sheet cultivosSheet = workbook.getSheet("Cultivos");
            if (cultivosSheet != null) {
                try {
                    cultivosCreados = importarCultivos(cultivosSheet);
                    logger.info("Cultivos importados: {}", cultivosCreados);
                } catch (Exception e) {
                    logger.error("Error al importar cultivos: {}", e.getMessage());
                    errores.add("Error en hoja Cultivos: " + e.getMessage());
                }
            }
            
            resultado.put("status", "success");
            resultado.put("usuarios", usuariosCreados);
            resultado.put("ganado", ganadoCreado);
            resultado.put("tratamientos", tratamientosCreados);
            resultado.put("cultivos", cultivosCreados);
            resultado.put("errores", errores);
            resultado.put("mensaje", String.format(
                "Importación completada: %d usuarios, %d ganado, %d tratamientos, %d cultivos",
                usuariosCreados, ganadoCreado, tratamientosCreados, cultivosCreados));
            
        } catch (Exception e) {
            logger.error("Error al procesar archivo Excel: {}", e.getMessage(), e);
            resultado.put("status", "error");
            resultado.put("mensaje", "Error al procesar el archivo Excel: " + e.getMessage());
            errores.add("Error general: " + e.getMessage());
            resultado.put("errores", errores);
        }
        
        return resultado;
    }
    
    /**
     * Importa usuarios desde la hoja "Usuarios"
     * Formato esperado: Nombre | Correo | Contraseña | Teléfono | Número Documento | Rol
     */
    private int importarUsuarios(Sheet sheet) {
        int creados = 0;
        
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            
            try {
                String nombre = getCellValueAsString(row.getCell(0));
                String correo = getCellValueAsString(row.getCell(1));
                String password = getCellValueAsString(row.getCell(2));
                String telefono = getCellValueAsString(row.getCell(3));
                String numeroDocumento = getCellValueAsString(row.getCell(4));
                String rolNombre = getCellValueAsString(row.getCell(5));
                
                if (correo == null || correo.trim().isEmpty()) {
                    continue; // Saltar filas sin correo
                }
                
                // Verificar si el usuario ya existe
                if (usuarioRepository.findByCorreo(correo).isPresent()) {
                    logger.warn("Usuario ya existe: {}", correo);
                    continue;
                }
                
                // Buscar o crear rol
                Role role = null;
                if (rolNombre != null && !rolNombre.trim().isEmpty()) {
                    role = roleRepository.findByNombre(rolNombre.toUpperCase())
                        .orElse(null);
                }
                
                // Crear usuario
                Usuario usuario = new Usuario();
                usuario.setNombre(nombre != null ? nombre.trim() : "Sin nombre");
                usuario.setCorreo(correo.trim());
                usuario.setPassword(passwordEncoder.encode(password != null ? password : "123456"));
                usuario.setTelefono(telefono);
                usuario.setNumeroDocumento(numeroDocumento);
                usuario.setRole(role);
                usuario.setActivo(true);
                usuario.setFechaCreacion(LocalDateTime.now());
                
                usuarioRepository.save(usuario);
                creados++;
                
            } catch (Exception e) {
                logger.error("Error al importar usuario en fila {}: {}", i + 1, e.getMessage());
            }
        }
        
        return creados;
    }
    
    /**
     * Importa ganado desde la hoja "Ganado"
     * Formato esperado: Tipo | Raza | Edad | Peso | Estado Salud | Fecha Nacimiento
     */
    private int importarGanado(Sheet sheet) {
        int creados = 0;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            
            try {
                String tipo = getCellValueAsString(row.getCell(0));
                String raza = getCellValueAsString(row.getCell(1));
                Integer edad = getCellValueAsInt(row.getCell(2));
                Double peso = getCellValueAsDouble(row.getCell(3));
                String estadoSalud = getCellValueAsString(row.getCell(4));
                String fechaNacStr = getCellValueAsString(row.getCell(5));
                
                if (tipo == null || tipo.trim().isEmpty()) {
                    continue; // Saltar filas sin tipo
                }
                
                Ganado ganado = new Ganado();
                ganado.setTipo(tipo.trim());
                ganado.setRaza(raza != null ? raza.trim() : "Mestizo");
                ganado.setEdad(edad != null ? edad : 1);
                ganado.setPeso(peso != null ? peso : 50.0);
                ganado.setEstadoSalud(estadoSalud != null ? estadoSalud.trim() : "Saludable");
                
                if (fechaNacStr != null && !fechaNacStr.trim().isEmpty()) {
                    try {
                        ganado.setFechaNacimiento(LocalDate.parse(fechaNacStr.trim(), dateFormatter));
                    } catch (Exception e) {
                        // Si no se puede parsear, usar fecha por defecto
                        ganado.setFechaNacimiento(LocalDate.now().minusYears(edad != null ? edad : 1));
                    }
                } else {
                    ganado.setFechaNacimiento(LocalDate.now().minusYears(edad != null ? edad : 1));
                }
                
                ganado.setFechaCreacion(LocalDateTime.now());
                ganado.setActivo(true);
                
                ganadoRepository.save(ganado);
                creados++;
                
            } catch (Exception e) {
                logger.error("Error al importar ganado en fila {}: {}", i + 1, e.getMessage());
            }
        }
        
        return creados;
    }
    
    /**
     * Importa tratamientos desde la hoja "Tratamientos"
     * Formato esperado: Tipo Tratamiento | Fecha | Observaciones | Veterinario | Costo | ID Ganado
     */
    private int importarTratamientos(Sheet sheet) {
        int creados = 0;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            
            try {
                String tipoTratamiento = getCellValueAsString(row.getCell(0));
                String fechaStr = getCellValueAsString(row.getCell(1));
                String observaciones = getCellValueAsString(row.getCell(2));
                String veterinario = getCellValueAsString(row.getCell(3));
                Double costo = getCellValueAsDouble(row.getCell(4));
                Long ganadoId = getCellValueAsLong(row.getCell(5));
                
                if (tipoTratamiento == null || tipoTratamiento.trim().isEmpty()) {
                    continue;
                }
                
                // Buscar ganado
                Ganado ganado = null;
                if (ganadoId != null) {
                    ganado = ganadoRepository.findById(ganadoId).orElse(null);
                }
                
                if (ganado == null) {
                    // Si no se encuentra, usar el primero disponible
                    List<Ganado> ganados = ganadoRepository.findAll();
                    if (!ganados.isEmpty()) {
                        ganado = ganados.get(0);
                    } else {
                        logger.warn("No hay ganado disponible para el tratamiento en fila {}", i + 1);
                        continue;
                    }
                }
                
                Tratamiento tratamiento = new Tratamiento();
                tratamiento.setGanado(ganado);
                tratamiento.setTipoTratamiento(tipoTratamiento.trim());
                
                if (fechaStr != null && !fechaStr.trim().isEmpty()) {
                    try {
                        tratamiento.setFechaTratamiento(LocalDate.parse(fechaStr.trim(), dateFormatter));
                    } catch (Exception e) {
                        tratamiento.setFechaTratamiento(LocalDate.now());
                    }
                } else {
                    tratamiento.setFechaTratamiento(LocalDate.now());
                }
                
                tratamiento.setObservaciones(observaciones != null ? observaciones.trim() : "");
                tratamiento.setVeterinarioResponsable(veterinario != null ? veterinario.trim() : "Dr. Veterinario");
                tratamiento.setCosto(costo != null ? BigDecimal.valueOf(costo) : BigDecimal.ZERO);
                tratamiento.setFechaCreacion(LocalDateTime.now());
                
                tratamientoRepository.save(tratamiento);
                creados++;
                
            } catch (Exception e) {
                logger.error("Error al importar tratamiento en fila {}: {}", i + 1, e.getMessage());
            }
        }
        
        return creados;
    }
    
    /**
     * Importa cultivos desde la hoja "Cultivos"
     * Formato esperado: Nombre | Tipo | Área | Estado | Fecha Siembra | Fecha Cosecha | Observaciones
     */
    private int importarCultivos(Sheet sheet) {
        int creados = 0;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            try {
                String nombre = getCellValueAsString(row.getCell(0));
                String tipo = getCellValueAsString(row.getCell(1));
                String area = getCellValueAsString(row.getCell(2));
                String estado = getCellValueAsString(row.getCell(3));
                String fechaSiembraStr = getCellValueAsString(row.getCell(4));
                String fechaCosechaStr = getCellValueAsString(row.getCell(5));
                String observaciones = getCellValueAsString(row.getCell(6));

                if (nombre == null || nombre.trim().isEmpty()) {
                    continue;
                }

                Cultivo cultivo = new Cultivo();
                cultivo.setNombre(nombre.trim());
                StringBuilder desc = new StringBuilder(observaciones != null ? observaciones.trim() : "");
                if (tipo != null && !tipo.trim().isEmpty()) {
                    if (desc.length() > 0) desc.append(" - ");
                    desc.append("Tipo: ").append(tipo.trim());
                }
                if (area != null && !area.trim().isEmpty()) {
                    if (desc.length() > 0) desc.append(" - ");
                    desc.append("Área: ").append(area.trim());
                }
                if (fechaSiembraStr != null && !fechaSiembraStr.trim().isEmpty()) {
                    if (desc.length() > 0) desc.append(" - ");
                    desc.append("Siembra: ").append(fechaSiembraStr.trim());
                }
                if (fechaCosechaStr != null && !fechaCosechaStr.trim().isEmpty()) {
                    if (desc.length() > 0) desc.append(" - ");
                    desc.append("Cosecha: ").append(fechaCosechaStr.trim());
                }
                cultivo.setDescripcion(desc.length() > 0 ? desc.toString() : "Sin descripción");
                cultivo.setEstado(estado != null && !estado.trim().isEmpty() ? estado.trim() : "Activo");
                cultivo.setActivo(estado == null || !estado.trim().equalsIgnoreCase("Inactivo"));
                if (fechaSiembraStr != null && !fechaSiembraStr.trim().isEmpty()) {
                    try {
                        cultivo.setFechaSiembra(LocalDate.parse(fechaSiembraStr.trim(), dateFormatter));
                    } catch (Exception ignored) { }
                }
                if (fechaCosechaStr != null && !fechaCosechaStr.trim().isEmpty()) {
                    try {
                        cultivo.setFechaCosecha(LocalDate.parse(fechaCosechaStr.trim(), dateFormatter));
                    } catch (Exception ignored) { }
                }
                cultivo.setTipo(tipo != null ? tipo.trim() : null);
                cultivo.setArea(area != null ? area.trim() : null);
                cultivo.setObservaciones(observaciones != null ? observaciones.trim() : null);
                cultivo.setFechaCreacion(LocalDateTime.now());
                
                cultivoRepository.save(cultivo);
                creados++;
                
            } catch (Exception e) {
                logger.error("Error al importar cultivo en fila {}: {}", i + 1, e.getMessage());
            }
        }
        
        return creados;
    }
    
    // Formato de fecha para celdas Excel (coincide con el usado al parsear en importarGanado/Tratamientos/Cultivos)
    private static final DateTimeFormatter FECHA_EXCEL = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Métodos auxiliares para leer celdas (UTF-8 y fechas consistentes)
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case STRING:
                String s = cell.getStringCellValue();
                return s != null ? s.trim() : null;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    try {
                        java.util.Date d = cell.getDateCellValue();
                        if (d == null) return null;
                        return new java.sql.Timestamp(d.getTime()).toLocalDateTime().toLocalDate().format(FECHA_EXCEL);
                    } catch (Exception e) {
                        return null;
                    }
                } else {
                    double numValue = cell.getNumericCellValue();
                    if (numValue == (long) numValue) {
                        return String.valueOf((long) numValue);
                    } else {
                        return String.valueOf(numValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return getCachedFormulaValueAsString(cell);
            default:
                return null;
        }
    }

    /** Devuelve el valor calculado de una celda fórmula (no la fórmula en texto). */
    private String getCachedFormulaValueAsString(Cell cell) {
        if (cell == null) return null;
        try {
            switch (cell.getCachedFormulaResultType()) {
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        try {
                            java.util.Date d = cell.getDateCellValue();
                            if (d == null) return null;
                            return new java.sql.Timestamp(d.getTime()).toLocalDateTime().toLocalDate().format(FECHA_EXCEL);
                        } catch (Exception e) {
                            return null;
                        }
                    }
                    double n = cell.getNumericCellValue();
                    return n == (long) n ? String.valueOf((long) n) : String.valueOf(n);
                case STRING:
                    return cell.getStringCellValue();
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                default:
                    return null;
            }
        } catch (Exception e) {
            logger.debug("No se pudo leer resultado de fórmula: {}", e.getMessage());
            return null;
        }
    }

    private Integer getCellValueAsInt(Cell cell) {
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return (int) cell.getNumericCellValue();
            } else if (cell.getCellType() == CellType.STRING) {
                return Integer.parseInt(cell.getStringCellValue().trim());
            }
        } catch (Exception e) {
            logger.warn("Error al convertir celda a entero: {}", e.getMessage());
        }
        return null;
    }
    
    private Double getCellValueAsDouble(Cell cell) {
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return cell.getNumericCellValue();
            } else if (cell.getCellType() == CellType.STRING) {
                return Double.parseDouble(cell.getStringCellValue().trim());
            }
        } catch (Exception e) {
            logger.warn("Error al convertir celda a double: {}", e.getMessage());
        }
        return null;
    }
    
    private Long getCellValueAsLong(Cell cell) {
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return (long) cell.getNumericCellValue();
            } else if (cell.getCellType() == CellType.STRING) {
                return Long.parseLong(cell.getStringCellValue().trim());
            }
        } catch (Exception e) {
            logger.warn("Error al convertir celda a long: {}", e.getMessage());
        }
        return null;
    }
}

