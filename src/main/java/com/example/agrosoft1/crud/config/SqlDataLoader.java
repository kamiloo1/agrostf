package com.example.agrosoft1.crud.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

import javax.sql.DataSource;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Carga datos desde el archivo SQL externo carga-inicial-datos_.sql
 * Este componente se ejecuta después del DataInitializer para cargar datos completos
 */
@Component
public class SqlDataLoader implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(SqlDataLoader.class);
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        cargarDatosDesdeSQL();
    }
    
    /**
     * Método público para cargar datos desde SQL (puede ser llamado manualmente)
     */
    @Transactional
    public void cargarDatosDesdeSQL() throws Exception {
        // Verificar si ya hay datos cargados
        try {
            Long usuarioCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM usuarios", Long.class);
            Long ganadoCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM ganado", Long.class);
            
            // Si ya hay más de 15 usuarios y más de 25 animales, asumimos que los datos completos ya están cargados
            if (usuarioCount != null && usuarioCount > 15 && ganadoCount != null && ganadoCount > 25) {
                logger.info("Los datos completos ya están cargados. Usuarios: {}, Ganado: {}. Saltando carga SQL.", usuarioCount, ganadoCount);
                return;
            }
            
            // Si hay pocos datos, cargar el script completo
            logger.info("Detectados pocos datos. Usuarios: {}, Ganado: {}. Procediendo a cargar datos completos...", usuarioCount, ganadoCount);
        } catch (Exception e) {
            logger.warn("No se pudo verificar datos existentes. Continuando con la carga...");
        }
        
        logger.info("=== Iniciando carga de datos desde archivo SQL ===");
        
        ejecutarCarga();
    }
    
    private void ejecutarCarga() {
        try {
            // Intentar cargar desde el archivo en el directorio raíz del proyecto
            java.io.File sqlFile = new java.io.File("carga-inicial-datos_.sql");
            
            if (!sqlFile.exists()) {
                logger.warn("Archivo carga-inicial-datos_.sql no encontrado en el directorio raíz.");
                logger.info("Para cargar datos completos, coloca el archivo carga-inicial-datos_.sql en la raíz del proyecto.");
                return;
            }
            
            logger.info("Archivo SQL encontrado: {}", sqlFile.getAbsolutePath());
            logger.info("Leyendo y ejecutando script SQL...");
            
            // Leer el archivo SQL
            String sqlScript = FileCopyUtils.copyToString(
                new InputStreamReader(
                    new java.io.FileInputStream(sqlFile),
                    StandardCharsets.UTF_8
                )
            );
            
            // Ejecutar el script
            ejecutarScriptSQL(sqlScript);
            
            logger.info("=== Carga de datos desde SQL completada exitosamente ===");
            
        } catch (Exception e) {
            logger.error("Error al cargar datos desde archivo SQL: {}", e.getMessage(), e);
            // No lanzar la excepción para que la aplicación continúe
        }
    }
    
    /**
     * Ejecuta un script SQL dividiéndolo en sentencias individuales
     */
    private void ejecutarScriptSQL(String sqlScript) throws Exception {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            
            // Dividir el script en sentencias individuales
            // Eliminar comentarios de una línea que empiezan con --
            String[] lines = sqlScript.split("\n");
            StringBuilder currentStatement = new StringBuilder();
            int statementsExecuted = 0;
            int statementsSkipped = 0;
            
            for (String line : lines) {
                // Saltar líneas vacías y comentarios
                String trimmedLine = line.trim();
                if (trimmedLine.isEmpty() || trimmedLine.startsWith("--") || trimmedLine.startsWith("/*")) {
                    continue;
                }
                
                // Agregar línea a la sentencia actual
                currentStatement.append(line).append("\n");
                
                // Si la línea termina con ;, ejecutar la sentencia
                if (trimmedLine.endsWith(";")) {
                    String sqlStatement = currentStatement.toString().trim();
                    
                    // Eliminar el punto y coma final
                    if (sqlStatement.endsWith(";")) {
                        sqlStatement = sqlStatement.substring(0, sqlStatement.length() - 1);
                    }
                    
                    if (!sqlStatement.isEmpty() && !sqlStatement.startsWith("--")) {
                        try {
                            statement.execute(sqlStatement);
                            statementsExecuted++;
                            if (statementsExecuted % 10 == 0) {
                                logger.debug("Ejecutadas {} sentencias...", statementsExecuted);
                            }
                        } catch (Exception e) {
                            // Algunos errores son esperados (como DROP TABLE IF EXISTS cuando no existe)
                            if (e.getMessage() != null && 
                                (e.getMessage().contains("Unknown database") || 
                                 e.getMessage().contains("doesn't exist"))) {
                                statementsSkipped++;
                                logger.debug("Sentencia omitida: {}", e.getMessage());
                            } else {
                                logger.warn("Error al ejecutar sentencia (puede ser esperado): {}", e.getMessage());
                                statementsSkipped++;
                            }
                        }
                    }
                    
                    // Reiniciar para la siguiente sentencia
                    currentStatement = new StringBuilder();
                }
            }
            
            logger.info("Script SQL ejecutado. Sentencias ejecutadas: {}, Omitidas: {}", statementsExecuted, statementsSkipped);
            
            // Verificar datos cargados
            try {
                Long usuarios = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM usuarios", Long.class);
                Long ganado = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM ganado", Long.class);
                Long cultivos = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cultivos", Long.class);
                Long tratamientos = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM tratamientos", Long.class);
                
                logger.info("=== Resumen de datos cargados ===");
                logger.info("Usuarios: {}", usuarios);
                logger.info("Ganado: {}", ganado);
                logger.info("Cultivos: {}", cultivos);
                logger.info("Tratamientos: {}", tratamientos);
            } catch (Exception e) {
                logger.warn("No se pudo verificar los datos cargados: {}", e.getMessage());
            }
            
        }
    }
}

