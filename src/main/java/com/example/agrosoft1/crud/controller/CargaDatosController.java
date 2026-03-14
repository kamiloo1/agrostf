package com.example.agrosoft1.crud.controller;

import com.example.agrosoft1.crud.entity.Ganado;
import com.example.agrosoft1.crud.repository.GanadoRepository;
import com.example.agrosoft1.crud.repository.RoleRepository;
import com.example.agrosoft1.crud.repository.TratamientoRepository;
import com.example.agrosoft1.crud.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para cargar datos de prueba directamente
 */
@RestController
@RequestMapping("/admin/data")
public class CargaDatosController {
    
    private static final Logger logger = LoggerFactory.getLogger(CargaDatosController.class);
    
    @Autowired
    private GanadoRepository ganadoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private TratamientoRepository tratamientoRepository;
    
    @PostMapping("/cargar-ganado")
    @Transactional
    public ResponseEntity<Map<String, Object>> cargarGanado() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            long antes = ganadoRepository.count();
            
            // Crear 30 animales
            String[] tipos = {"Vaca", "Cerdo", "Oveja", "Cabra", "Caballo", "Pollo", "Pavo"};
            String[] razasVaca = {"Holstein", "Jersey", "Angus", "Hereford", "Brahman"};
            String[] razasCerdo = {"Yorkshire", "Landrace", "Duroc", "Hampshire"};
            String[] razasOveja = {"Dorper", "Merino", "Suffolk", "Texel"};
            String[] estados = {"Saludable", "Saludable", "Saludable", "En observación", "En Tratamiento"};
            
            int creados = 0;
            for (int i = 0; i < 30; i++) {
                String tipo = tipos[i % tipos.length];
                String raza = "";
                int edad = 1 + (i % 8);
                double peso = 50.0 + (i * 15.5);
                String estado = estados[i % estados.length];
                
                switch (tipo) {
                    case "Vaca":
                        raza = razasVaca[i % razasVaca.length];
                        peso = 300.0 + (i * 20.0);
                        break;
                    case "Cerdo":
                        raza = razasCerdo[i % razasCerdo.length];
                        peso = 80.0 + (i * 10.0);
                        break;
                    case "Oveja":
                        raza = razasOveja[i % razasOveja.length];
                        peso = 40.0 + (i * 5.0);
                        break;
                    default:
                        raza = "Mestizo";
                        break;
                }
                
                LocalDate fechaNac = LocalDate.now().minusYears(edad).minusMonths(i % 12);
                
                Ganado ganado = new Ganado();
                ganado.setTipo(tipo);
                ganado.setRaza(raza);
                ganado.setEdad(edad);
                ganado.setPeso(peso);
                ganado.setEstadoSalud(estado);
                ganado.setFechaNacimiento(fechaNac);
                ganado.setFechaCreacion(LocalDateTime.now());
                ganado.setActivo(true);
                
                ganadoRepository.save(ganado);
                creados++;
            }
            
            long despues = ganadoRepository.count();
            
            response.put("status", "success");
            response.put("mensaje", "Ganado cargado exitosamente");
            response.put("antes", antes);
            response.put("creados", creados);
            response.put("despues", despues);
            
            logger.info("Cargados {} animales. Total en BD: {}", creados, despues);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al cargar ganado: {}", e.getMessage(), e);
            response.put("status", "error");
            response.put("mensaje", "Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @GetMapping("/verificar")
    public ResponseEntity<Map<String, Object>> verificarDatos() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            long ganado = ganadoRepository.count();
            long usuarios = usuarioRepository.count();
            long tratamientos = tratamientoRepository.count();
            long roles = roleRepository.count();
            
            response.put("status", "success");
            response.put("ganado", ganado);
            response.put("usuarios", usuarios);
            response.put("tratamientos", tratamientos);
            response.put("roles", roles);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al verificar datos: {}", e.getMessage(), e);
            response.put("status", "error");
            response.put("mensaje", "Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}

