package com.example.agrosoft1.crud.controller;

import com.example.agrosoft1.crud.config.SqlDataLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para cargar datos manualmente desde el archivo SQL
 */
@RestController
@RequestMapping("/admin/data")
public class DataLoadController {
    
    private static final Logger logger = LoggerFactory.getLogger(DataLoadController.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private SqlDataLoader sqlDataLoader;
    
    /**
     * Endpoint para cargar datos desde el archivo SQL
     * POST /admin/data/load
     */
    @PostMapping("/load")
    public ResponseEntity<Map<String, Object>> cargarDatos() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("Iniciando carga de datos desde endpoint...");
            
            // Verificar datos actuales
            Long usuariosAntes = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM usuarios", Long.class);
            Long ganadoAntes = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM ganado", Long.class);
            Long cultivosAntes = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cultivos", Long.class);
            
            response.put("antes", Map.of(
                "usuarios", usuariosAntes != null ? usuariosAntes : 0,
                "ganado", ganadoAntes != null ? ganadoAntes : 0,
                "cultivos", cultivosAntes != null ? cultivosAntes : 0
            ));
            
            // Ejecutar carga de datos
            sqlDataLoader.cargarDatosDesdeSQL();
            
            // Verificar datos después
            Long usuariosDespues = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM usuarios", Long.class);
            Long ganadoDespues = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM ganado", Long.class);
            Long cultivosDespues = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cultivos", Long.class);
            Long tratamientosDespues = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM tratamientos", Long.class);
            Long actividadesDespues = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM actividades", Long.class);
            Long fincasDespues = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM fincas", Long.class);
            
            response.put("despues", Map.of(
                "usuarios", usuariosDespues != null ? usuariosDespues : 0,
                "ganado", ganadoDespues != null ? ganadoDespues : 0,
                "cultivos", cultivosDespues != null ? cultivosDespues : 0,
                "tratamientos", tratamientosDespues != null ? tratamientosDespues : 0,
                "actividades", actividadesDespues != null ? actividadesDespues : 0,
                "fincas", fincasDespues != null ? fincasDespues : 0
            ));
            
            response.put("status", "success");
            response.put("message", "Datos cargados exitosamente");
            
            logger.info("Carga de datos completada desde endpoint");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al cargar datos desde endpoint: {}", e.getMessage(), e);
            response.put("status", "error");
            response.put("message", "Error al cargar datos: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * Endpoint para verificar el estado de los datos
     * GET /admin/data/status
     */
    @PostMapping("/status")
    public ResponseEntity<Map<String, Object>> estadoDatos() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Long> datos = new HashMap<>();
            
            datos.put("roles", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM roles", Long.class));
            datos.put("usuarios", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM usuarios", Long.class));
            datos.put("fincas", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM fincas", Long.class));
            datos.put("cultivos", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cultivos", Long.class));
            datos.put("siembras", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM siembras", Long.class));
            datos.put("ganado", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM ganado", Long.class));
            datos.put("tratamientos", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM tratamientos", Long.class));
            datos.put("actividades", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM actividades", Long.class));
            datos.put("abonos", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM abonos", Long.class));
            datos.put("plagas", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM plagas", Long.class));
            datos.put("tareas", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM tareas", Long.class));
            
            response.put("status", "success");
            response.put("datos", datos);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al verificar estado de datos: {}", e.getMessage(), e);
            response.put("status", "error");
            response.put("message", "Error al verificar datos: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}

