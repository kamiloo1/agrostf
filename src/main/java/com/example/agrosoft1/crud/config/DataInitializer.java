package com.example.agrosoft1.crud.config;

import com.example.agrosoft1.crud.entity.Ganado;
import com.example.agrosoft1.crud.entity.Role;
import com.example.agrosoft1.crud.entity.Tratamiento;
import com.example.agrosoft1.crud.entity.Usuario;
import com.example.agrosoft1.crud.gof.UsuarioFactory;
import com.example.agrosoft1.crud.repository.GanadoRepository;
import com.example.agrosoft1.crud.repository.RoleRepository;
import com.example.agrosoft1.crud.repository.TratamientoRepository;
import com.example.agrosoft1.crud.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * CommandLineRunner para carga inicial de datos al iniciar el sistema.
 * 
 * Este componente se ejecuta automáticamente cuando la aplicación Spring Boot inicia.
 * Crea datos de ejemplo incluyendo:
 * - Roles del sistema (ADMIN, VETERINARIO, TRABAJADOR)
 * - Usuario administrador por defecto
 * - Mínimo un veterinario y un trabajador agrícola
 * - Pacientes (ganado) y tratamientos de ejemplo
 * 
 * El inicializador verifica si ya existen datos para evitar duplicación.
 * 
 * Documentado en el informe técnico como parte de la funcionalidad de inicialización.
 */
@Component
public class DataInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final GanadoRepository ganadoRepository;
    private final TratamientoRepository tratamientoRepository;
    private final UsuarioFactory usuarioFactory;
    
    public DataInitializer(UsuarioRepository usuarioRepository,
                          RoleRepository roleRepository,
                          GanadoRepository ganadoRepository,
                          TratamientoRepository tratamientoRepository,
                          UsuarioFactory usuarioFactory) {
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.ganadoRepository = ganadoRepository;
        this.tratamientoRepository = tratamientoRepository;
        this.usuarioFactory = usuarioFactory;
    }
    
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger.info("=== Iniciando carga de datos iniciales ===");
        
        try {
            // Verificar si ya existen datos para no duplicar
            if (usuarioRepository.count() > 0) {
                logger.info("Ya existen usuarios en la base de datos. Saltando carga inicial.");
                return;
            }
            
            // 1. Crear roles si no existen
            crearRoles();
            
            // 2. Crear usuarios por defecto
            crearUsuariosIniciales();
            
            // 3. Crear pacientes (ganado) de ejemplo
            crearPacientesEjemplo();
            
            // 4. Crear tratamientos de ejemplo
            crearTratamientosEjemplo();
            
            logger.info("=== Carga de datos iniciales completada exitosamente ===");
        } catch (Exception e) {
            logger.error("Error durante la carga inicial de datos: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Crea los roles del sistema si no existen
     */
    private void crearRoles() {
        logger.info("Creando roles del sistema...");
        
        crearRolSiNoExiste("ADMIN", "Administrador con todos los permisos");
        crearRolSiNoExiste("VETERINARIO", "Usuario con permisos veterinarios");
        crearRolSiNoExiste("TRABAJADOR", "Trabajador agrícola");
        
        logger.info("Roles del sistema verificados/creados correctamente");
    }
    
    /**
     * Crea un rol si no existe
     */
    private void crearRolSiNoExiste(String nombre, String descripcion) {
        if (roleRepository.findByNombre(nombre).isEmpty()) {
            Role role = new Role();
            role.setNombre(nombre);
            role.setDescripcion(descripcion);
            roleRepository.save(role);
            logger.info("Rol {} creado", nombre);
        } else {
            logger.debug("Rol {} ya existe", nombre);
        }
    }
    
    /**
     * Crea los usuarios iniciales del sistema
     */
    private void crearUsuariosIniciales() {
        logger.info("Creando usuarios iniciales del sistema...");
        
        // Obtener roles (deben existir después de crearRoles())
        Role adminRole = roleRepository.findByNombre("ADMIN")
                .orElseThrow(() -> new IllegalStateException("Rol ADMIN no encontrado. Debe crearse primero."));
        Role vetRole = roleRepository.findByNombre("VETERINARIO")
                .orElseThrow(() -> new IllegalStateException("Rol VETERINARIO no encontrado. Debe crearse primero."));
        Role trabajadorRole = roleRepository.findByNombre("TRABAJADOR")
                .orElseThrow(() -> new IllegalStateException("Rol TRABAJADOR no encontrado. Debe crearse primero."));
        
        // Usuario administrador por defecto
        crearUsuarioSiNoExiste(
            "admin@agrosoft.local",
            "Administrador Principal",
            "admin123",
            "+57-3000000000",
            "1234567890",
            adminRole,
            "ADMIN"
        );
        
        // Mínimo un veterinario
        crearUsuarioSiNoExiste(
            "veterinario@agrosoft.local",
            "Dr. Carlos Veterinario",
            "vet123",
            "+57-3111111111",
            "2345678901",
            vetRole,
            "VETERINARIO"
        );
        
        // Mínimo un trabajador agrícola
        crearUsuarioSiNoExiste(
            "trabajador@agrosoft.local",
            "Juan Trabajador",
            "trab123",
            "+57-3222222222",
            "3456789012",
            trabajadorRole,
            "TRABAJADOR"
        );
        
        logger.info("Usuarios iniciales verificados/creados correctamente");
    }
    
    /**
     * Crea un usuario si no existe
     */
    private void crearUsuarioSiNoExiste(String correo, String nombre, String password,
                                        String telefono, String numeroDocumento,
                                        Role role, String tipoUsuario) {
        if (usuarioRepository.findByCorreo(correo).isEmpty()) {
            Usuario usuario;
            
            switch (tipoUsuario.toUpperCase()) {
                case "ADMIN":
                    usuario = usuarioFactory.crearAdministrador(nombre, correo, password, telefono, numeroDocumento, role);
                    break;
                case "VETERINARIO":
                    usuario = usuarioFactory.crearVeterinario(nombre, correo, password, telefono, numeroDocumento, role);
                    break;
                case "TRABAJADOR":
                    usuario = usuarioFactory.crearTrabajador(nombre, correo, password, telefono, numeroDocumento, role);
                    break;
                default:
                    throw new IllegalArgumentException("Tipo de usuario no válido: " + tipoUsuario);
            }
            
            usuarioRepository.save(usuario);
            logger.info("Usuario {} creado: {} / {}", tipoUsuario.toLowerCase(), correo, password);
        } else {
            logger.debug("Usuario {} ya existe", correo);
        }
    }
    
    /**
     * Crea pacientes (ganado) de ejemplo
     */
    private void crearPacientesEjemplo() {
        logger.info("Creando pacientes (ganado) de ejemplo...");
        
        if (ganadoRepository.count() > 0) {
            logger.info("Ya existen registros de ganado. Saltando creación de pacientes de ejemplo.");
            return;
        }
        
        // Paciente 1: Vaca Holstein
        Ganado ganado1 = crearGanado(
            "Vaca",
            "Holstein",
            5,
            450.0,
            "Saludable",
            LocalDate.of(2019, 3, 15)
        );
        ganadoRepository.save(ganado1);
        logger.info("Paciente creado: {} {}", ganado1.getTipo(), ganado1.getRaza());
        
        // Paciente 2: Cerdo Yorkshire
        Ganado ganado2 = crearGanado(
            "Cerdo",
            "Yorkshire",
            2,
            120.0,
            "En Tratamiento",
            LocalDate.of(2022, 6, 20)
        );
        ganadoRepository.save(ganado2);
        logger.info("Paciente creado: {} {}", ganado2.getTipo(), ganado2.getRaza());
        
        // Paciente 3: Vaca Jersey
        Ganado ganado3 = crearGanado(
            "Vaca",
            "Jersey",
            3,
            380.0,
            "Saludable",
            LocalDate.of(2021, 1, 10)
        );
        ganadoRepository.save(ganado3);
        logger.info("Paciente creado: {} {}", ganado3.getTipo(), ganado3.getRaza());
        
        logger.info("Pacientes de ejemplo creados correctamente");
    }
    
    /**
     * Método helper para crear un objeto Ganado con valores por defecto
     */
    private Ganado crearGanado(String tipo, String raza, Integer edad, Double peso,
                               String estadoSalud, LocalDate fechaNacimiento) {
        Ganado ganado = new Ganado();
        ganado.setTipo(tipo);
        ganado.setRaza(raza);
        ganado.setEdad(edad);
        ganado.setPeso(peso);
        ganado.setEstadoSalud(estadoSalud);
        ganado.setFechaNacimiento(fechaNacimiento);
        ganado.setFechaCreacion(LocalDateTime.now());
        ganado.setActivo(true);
        return ganado;
    }
    
    /**
     * Crea tratamientos de ejemplo asociados a los pacientes
     */
    private void crearTratamientosEjemplo() {
        logger.info("Creando tratamientos de ejemplo...");
        
        if (tratamientoRepository.count() > 0) {
            logger.info("Ya existen tratamientos. Saltando creación de tratamientos de ejemplo.");
            return;
        }
        
        // Obtener pacientes creados
        List<Ganado> ganados = ganadoRepository.findAll();
        if (ganados.isEmpty()) {
            logger.warn("No hay ganado disponible para crear tratamientos. Saltando creación de tratamientos.");
            return;
        }
        
        Ganado ganado1 = ganados.get(0);
        Ganado ganado2 = ganados.size() > 1 ? ganados.get(1) : ganados.get(0);
        
        // Tratamiento 1: Vacunación
        Tratamiento tratamiento1 = crearTratamiento(
            ganado1,
            "Vacunación",
            LocalDate.now().minusDays(10),
            "Vacunación anual contra fiebre aftosa",
            "Dr. Carlos Veterinario",
            new BigDecimal("50000")
        );
        tratamientoRepository.save(tratamiento1);
        logger.info("Tratamiento creado: {} para {}", tratamiento1.getTipoTratamiento(), ganado1.getTipo());
        
        // Tratamiento 2: Desparasitación
        Tratamiento tratamiento2 = crearTratamiento(
            ganado2,
            "Desparasitación",
            LocalDate.now().minusDays(5),
            "Tratamiento antiparasitario completo",
            "Dr. Carlos Veterinario",
            new BigDecimal("35000")
        );
        tratamientoRepository.save(tratamiento2);
        logger.info("Tratamiento creado: {} para {}", tratamiento2.getTipoTratamiento(), ganado2.getTipo());
        
        // Tratamiento 3: Antibiótico (activo)
        Tratamiento tratamiento3 = crearTratamiento(
            ganado2,
            "Antibiótico",
            LocalDate.now(),
            "Tratamiento por infección respiratoria",
            "Dr. Carlos Veterinario",
            new BigDecimal("75000")
        );
        tratamientoRepository.save(tratamiento3);
        logger.info("Tratamiento activo creado: {} para {}", tratamiento3.getTipoTratamiento(), ganado2.getTipo());
        
        logger.info("Tratamientos de ejemplo creados correctamente");
    }
    
    /**
     * Método helper para crear un objeto Tratamiento
     */
    private Tratamiento crearTratamiento(Ganado ganado, String tipoTratamiento,
                                         LocalDate fechaTratamiento, String observaciones,
                                         String veterinarioResponsable, BigDecimal costo) {
        Tratamiento tratamiento = new Tratamiento();
        tratamiento.setGanado(ganado);
        tratamiento.setTipoTratamiento(tipoTratamiento);
        tratamiento.setFechaTratamiento(fechaTratamiento);
        tratamiento.setObservaciones(observaciones);
        tratamiento.setVeterinarioResponsable(veterinarioResponsable);
        tratamiento.setCosto(costo);
        tratamiento.setFechaCreacion(LocalDateTime.now());
        return tratamiento;
    }
}
