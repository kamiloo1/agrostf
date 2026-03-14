package com.example.agrosoft1.crud.config;

import com.example.agrosoft1.crud.entity.Ganado;
import com.example.agrosoft1.crud.entity.PlantillaCorreo;
import com.example.agrosoft1.crud.entity.Role;
import com.example.agrosoft1.crud.entity.Tratamiento;
import com.example.agrosoft1.crud.entity.Usuario;
import com.example.agrosoft1.crud.gof.UsuarioFactory;
import com.example.agrosoft1.crud.repository.GanadoRepository;
import com.example.agrosoft1.crud.repository.NotificacionRepository;
import com.example.agrosoft1.crud.repository.PlantillaCorreoRepository;
import com.example.agrosoft1.crud.repository.RoleRepository;
import com.example.agrosoft1.crud.repository.TratamientoRepository;
import com.example.agrosoft1.crud.repository.UsuarioRepository;
import com.example.agrosoft1.crud.service.NotificacionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    /** Mínimo de usuarios que debe haber (admin, veterinario, trabajador) */
    private static final int NUM_USUARIOS_MINIMOS = 3;
    /** Objetivo de usuarios para datos de ejemplo */
    private static final int NUM_USUARIOS_OBJETIVO = 15;
    /** Si hay al menos este ganado y usuarios mínimos, se considera carga completa */
    private static final long GANADO_UMBRAL_COMPLETO = 25;
    /** Objetivo de animales de ejemplo */
    private static final int GANADO_OBJETIVO = 30;
    /** Objetivo de tratamientos de ejemplo */
    private static final int TRATAMIENTOS_OBJETIVO = 25;

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final GanadoRepository ganadoRepository;
    private final TratamientoRepository tratamientoRepository;
    private final PlantillaCorreoRepository plantillaCorreoRepository;
    private final UsuarioFactory usuarioFactory;
    private final PasswordEncoder passwordEncoder;
    private final NotificacionService notificacionService;
    private final NotificacionRepository notificacionRepository;

    public DataInitializer(UsuarioRepository usuarioRepository,
                          RoleRepository roleRepository,
                          GanadoRepository ganadoRepository,
                          TratamientoRepository tratamientoRepository,
                          PlantillaCorreoRepository plantillaCorreoRepository,
                          UsuarioFactory usuarioFactory,
                          PasswordEncoder passwordEncoder,
                          NotificacionService notificacionService,
                          NotificacionRepository notificacionRepository) {
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.ganadoRepository = ganadoRepository;
        this.tratamientoRepository = tratamientoRepository;
        this.plantillaCorreoRepository = plantillaCorreoRepository;
        this.usuarioFactory = usuarioFactory;
        this.passwordEncoder = passwordEncoder;
        this.notificacionService = notificacionService;
        this.notificacionRepository = notificacionRepository;
    }
    
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger.info("=== Iniciando carga de datos iniciales ===");
        
        try {
            // SIEMPRE cargar datos si hay menos de 30 animales
            long usuarioCount = usuarioRepository.count();
            long ganadoCount = ganadoRepository.count();
            
            logger.info("Estado actual - Usuarios: {}, Ganado: {}", usuarioCount, ganadoCount);
            
            // Siempre asegurar roles y al menos los 3 usuarios básicos (admin, vet, trabajador)
            crearRoles();
            if (usuarioCount < NUM_USUARIOS_MINIMOS) {
                logger.info("Menos de {} usuarios ({}). Creando usuarios iniciales obligatorios.", NUM_USUARIOS_MINIMOS, usuarioCount);
                crearUsuariosIniciales();
            }
            // Normalizar contraseña de admins a "123456" (por si se cargó desde SQL con otro hash)
            normalizarPasswordAdmins();

            // Si ya hay suficientes animales y usuarios, no cargar más datos (excepto tratamientos si faltan)
            if (ganadoCount >= GANADO_UMBRAL_COMPLETO && usuarioCount >= NUM_USUARIOS_MINIMOS) {
                logger.info("Datos completos ya cargados. Ganado: {}, Usuarios: {}.", ganadoCount, usuarioCount);
                long tratCount = tratamientoRepository.count();
                if (tratCount < 9) {
                    logger.info("Solo hay {} tratamientos. Creando 9 tratamientos de prueba...", tratCount);
                    crearTratamientosDePrueba(9);
                }
                crearNotificacionesIniciales();
                return;
            }

            if (ganadoCount < GANADO_UMBRAL_COMPLETO) {
                logger.info("Detectados pocos animales ({}). Cargando ganado y tratamientos...", ganadoCount);
            }

            // Crear usuarios adicionales hasta el objetivo si hay pocos
            if (usuarioCount < NUM_USUARIOS_OBJETIVO) {
                crearUsuariosIniciales();
            }
            
            // 3. Crear pacientes (ganado) de ejemplo
            crearPacientesEjemplo();
            
            // 4. Crear tratamientos de ejemplo
            crearTratamientosEjemplo();
            
            // 5. Crear plantillas de correo predeterminadas
            crearPlantillasCorreo();
            
            logger.info("=== Carga de datos iniciales completada exitosamente ===");
        } catch (Exception e) {
            logger.error("Error durante la carga inicial de datos: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /** Crea notificaciones de bienvenida para el administrador (solo si no tiene ninguna). */
    private void crearNotificacionesIniciales() {
        if (notificacionService == null) return;
        usuarioRepository.findByCorreo("admin@agrosoft.local")
                .or(() -> usuarioRepository.findByCorreo("admin@agrosoft.com"))
                .ifPresent(admin -> {
                    long count = notificacionRepository.countByUsuario(admin);
                    if (count == 0) {
                        notificacionService.crear(admin, "Bienvenido a AgroSoft. El sistema está listo para usar.", "SISTEMA", "/dashboard/administrador");
                        notificacionService.crear(admin, "Puedes gestionar ganado, cultivos y tratamientos desde el panel.", "SISTEMA", "/admin/ganado");
                        notificacionService.crear(admin, "Genera reportes estadísticos en PDF desde la sección Reportes.", "SISTEMA", "/admin/reportes");
                        logger.info("Notificaciones de bienvenida creadas para admin");
                    }
                });
    }

    /** Asegura que los usuarios de prueba tengan contraseña "123456" (por si se cargó desde SQL). */
    private void normalizarPasswordAdmins() {
        String pass123456 = passwordEncoder.encode("123456");
        for (String correo : new String[]{"admin@agrosoft.com", "admin@agrosoft.local",
                "veterinario@agrosoft.com", "veterinario@agrosoft.local",
                "trabajador@agrosoft.com", "trabajador@agrosoft.local"}) {
            usuarioRepository.findByCorreo(correo).ifPresent(u -> {
                u.setPassword(pass123456);
                usuarioRepository.save(u);
                logger.info("Contraseña normalizada a 123456 para: {}", correo);
            });
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
        
        // Crear más usuarios hasta el objetivo si hay pocos
        if (usuarioRepository.count() < NUM_USUARIOS_OBJETIVO) {
            logger.info("Creando usuarios adicionales...");
            crearUsuariosAdicionales(adminRole, vetRole, trabajadorRole);
        }
        
        logger.info("Usuarios iniciales verificados/creados correctamente");
    }
    
    /**
     * Crea un usuario si no existe
     */
    @SuppressWarnings("null")
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
    @SuppressWarnings("null")
    private void crearPacientesEjemplo() {
        logger.info("Creando pacientes (ganado) de ejemplo...");
        
        long countInicial = ganadoRepository.count();
        logger.info("Animales existentes al inicio: {}", countInicial);
        
        // Crear animales hasta llegar al objetivo, sin importar si ya hay algunos
        if (countInicial < GANADO_OBJETIVO) {
            logger.info("Creando animales hasta llegar a {}. Actual: {}, Faltan: {}", GANADO_OBJETIVO, countInicial, (GANADO_OBJETIVO - countInicial));
            crearAnimalesAdicionales();
            long countFinal = ganadoRepository.count();
            logger.info("Total de animales después de carga: {} (objetivo: {})", countFinal, GANADO_OBJETIVO);
        } else {
            logger.info("Ya hay suficientes animales: {}", countInicial);
        }
        
        logger.info("Pacientes de ejemplo creados correctamente");
    }
    
    /**
     * Crea usuarios adicionales para tener datos completos
     */
    private void crearUsuariosAdicionales(Role adminRole, Role vetRole, Role trabajadorRole) {
        // Más administradores
        crearUsuarioSiNoExiste("maria.admin@agrosoft.local", "María González", "admin123", "+57-3000000001", "1234567891", adminRole, "ADMIN");
        crearUsuarioSiNoExiste("carlos.admin@agrosoft.local", "Carlos Ramírez", "admin123", "+57-3000000002", "1234567892", adminRole, "ADMIN");
        
        // Más veterinarios
        crearUsuarioSiNoExiste("marta.vet@agrosoft.local", "Dra. Marta López", "vet123", "+57-3111111112", "2345678902", vetRole, "VETERINARIO");
        crearUsuarioSiNoExiste("juan.vet@agrosoft.local", "Dr. Juan Pérez", "vet123", "+57-3111111113", "2345678903", vetRole, "VETERINARIO");
        crearUsuarioSiNoExiste("ana.vet@agrosoft.local", "Dra. Ana Martínez", "vet123", "+57-3111111114", "2345678904", vetRole, "VETERINARIO");
        crearUsuarioSiNoExiste("luis.vet@agrosoft.local", "Dr. Luis Fernández", "vet123", "+57-3111111115", "2345678905", vetRole, "VETERINARIO");
        
        // Más trabajadores
        crearUsuarioSiNoExiste("maria.trab@agrosoft.local", "María Obrera", "trab123", "+57-3222222223", "3456789013", trabajadorRole, "TRABAJADOR");
        crearUsuarioSiNoExiste("jose.trab@agrosoft.local", "José Campesino", "trab123", "+57-3222222224", "3456789014", trabajadorRole, "TRABAJADOR");
        crearUsuarioSiNoExiste("carmen.trab@agrosoft.local", "Carmen Agricultora", "trab123", "+57-3222222225", "3456789015", trabajadorRole, "TRABAJADOR");
        crearUsuarioSiNoExiste("roberto.trab@agrosoft.local", "Roberto Jornalero", "trab123", "+57-3222222226", "3456789016", trabajadorRole, "TRABAJADOR");
        crearUsuarioSiNoExiste("laura.trab@agrosoft.local", "Laura Siembra", "trab123", "+57-3222222227", "3456789017", trabajadorRole, "TRABAJADOR");
        crearUsuarioSiNoExiste("miguel.trab@agrosoft.local", "Miguel Cultivo", "trab123", "+57-3222222228", "3456789018", trabajadorRole, "TRABAJADOR");
    }
    
    /**
     * Crea animales adicionales para tener datos completos
     */
    private void crearAnimalesAdicionales() {
        String[] tipos = {"Vaca", "Cerdo", "Oveja", "Cabra", "Caballo", "Pollo", "Pavo"};
        String[] razasVaca = {"Holstein", "Jersey", "Angus", "Hereford", "Brahman"};
        String[] razasCerdo = {"Yorkshire", "Landrace", "Duroc", "Hampshire"};
        String[] razasOveja = {"Dorper", "Merino", "Suffolk", "Texel"};
        String[] estados = {"Saludable", "Saludable", "Saludable", "En observación", "En Tratamiento"};
        
        long countInicial = ganadoRepository.count();
        int animalesCreados = 0;
        int aCrear = (int) (GANADO_OBJETIVO - countInicial);
        
        logger.info("Iniciando creación de {} animales adicionales (actual: {})", aCrear, countInicial);
        
        for (int i = 0; i < aCrear; i++) {
            // Usar índice absoluto para variar los datos
            int indiceAbsoluto = (int)countInicial + i;
            
            String tipo = tipos[indiceAbsoluto % tipos.length];
            String raza = "";
            int edad = 1 + (indiceAbsoluto % 8);
            double peso = 50.0 + (indiceAbsoluto * 15.5);
            String estado = estados[indiceAbsoluto % estados.length];
            
            switch (tipo) {
                case "Vaca":
                    raza = razasVaca[indiceAbsoluto % razasVaca.length];
                    peso = 300.0 + (indiceAbsoluto * 20.0);
                    break;
                case "Cerdo":
                    raza = razasCerdo[indiceAbsoluto % razasCerdo.length];
                    peso = 80.0 + (indiceAbsoluto * 10.0);
                    break;
                case "Oveja":
                    raza = razasOveja[indiceAbsoluto % razasOveja.length];
                    peso = 40.0 + (indiceAbsoluto * 5.0);
                    break;
                default:
                    raza = "Mestizo";
                    break;
            }
            
            LocalDate fechaNac = LocalDate.now().minusYears(edad).minusMonths(indiceAbsoluto % 12);
            
            try {
                Ganado ganado = crearGanado(tipo, raza, edad, peso, estado, fechaNac);
                if (ganado != null) {
                    ganadoRepository.save(ganado);
                    animalesCreados++;
                }
                
                if (animalesCreados % 5 == 0) {
                    logger.info("Creados {} animales adicionales... (total actual: {})", animalesCreados, ganadoRepository.count());
                }
            } catch (Exception e) {
                logger.error("Error al crear animal {}: {}", i, e.getMessage());
            }
        }
        
        long countFinal = ganadoRepository.count();
        logger.info("✓ Proceso completado. Creados: {} animales. Total en BD: {} (objetivo: {})", animalesCreados, countFinal, GANADO_OBJETIVO);
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
     * Crea exactamente N tratamientos de prueba cuando hay pocos o ninguno.
     */
    @SuppressWarnings("null")
    private void crearTratamientosDePrueba(int cantidad) {
        List<Ganado> ganados = ganadoRepository.findAll();
        if (ganados.isEmpty()) {
            logger.warn("No hay ganado para crear tratamientos de prueba.");
            return;
        }
        String[] tipos = {"Vacunación", "Desparasitación", "Antibiótico", "Cirugía", "Revisión General",
                "Tratamiento de Heridas", "Control Reproductivo", "Vacunación", "Desparasitación"};
        String[] observaciones = {
            "Vacunación anual contra fiebre aftosa. Animal en buen estado.",
            "Desparasitación trimestral aplicada correctamente.",
            "Tratamiento por infección respiratoria. Seguimiento en 5 días.",
            "Castración programada. Recuperación sin complicaciones.",
            "Revisión general de rutina. Peso y condición óptimos.",
            "Curación de herida en pata posterior. Vendaje aplicado.",
            "Control de ciclo reproductivo. Todo normal.",
            "Refuerzo vacunal contra carbunco.",
            "Segunda dosis antiparasitaria del ciclo."
        };
        String[] veterinarios = {"Dr. Carlos Veterinario", "Dra. Marta López", "Dr. Juan Pérez"};
        double[] costos = {50000, 35000, 75000, 120000, 25000, 45000, 55000, 48000, 32000};
        for (int i = 0; i < cantidad; i++) {
            Ganado g = ganados.get(i % ganados.size());
            Tratamiento t = crearTratamiento(g, tipos[i], LocalDate.now().minusDays(i * 3),
                    observaciones[i], veterinarios[i % veterinarios.length], BigDecimal.valueOf(costos[i]));
            tratamientoRepository.save(t);
        }
        logger.info("Creados {} tratamientos de prueba.", cantidad);
    }

    /**
     * Crea tratamientos de ejemplo asociados a los pacientes
     */
    @SuppressWarnings("null")
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
        
        // Crear más tratamientos hasta el objetivo si hay pocos
        if (tratamientoRepository.count() < TRATAMIENTOS_OBJETIVO) {
            logger.info("Creando tratamientos adicionales...");
            crearTratamientosAdicionales();
        }
        
        logger.info("Tratamientos de ejemplo creados correctamente");
    }
    
    /**
     * Crea tratamientos adicionales
     */
    private void crearTratamientosAdicionales() {
        List<Ganado> ganados = ganadoRepository.findAll();
        if (ganados.isEmpty()) return;
        
        String[] tiposTratamiento = {"Vacunación", "Desparasitación", "Antibiótico", "Vitaminas", "Control de Peso", "Revisión General"};
        String[] veterinarios = {"Dr. Carlos Veterinario", "Dra. Marta López", "Dr. Juan Pérez", "Dra. Ana Martínez"};
        
        int tratamientosCreados = 0;
        final int maxIntentos = 30;

        while (tratamientoRepository.count() < TRATAMIENTOS_OBJETIVO && tratamientosCreados < maxIntentos) {
            Ganado ganado = ganados.get(tratamientosCreados % ganados.size());
            String tipo = tiposTratamiento[tratamientosCreados % tiposTratamiento.length];
            LocalDate fecha = LocalDate.now().minusDays(tratamientosCreados % 30);
            String observaciones = "Tratamiento " + tipo.toLowerCase() + " aplicado correctamente";
            String veterinario = veterinarios[tratamientosCreados % veterinarios.length];
            BigDecimal costo = new BigDecimal(30000 + (tratamientosCreados * 5000));
            
            Tratamiento tratamiento = crearTratamiento(ganado, tipo, fecha, observaciones, veterinario, costo);
            if (tratamiento != null) {
                tratamientoRepository.save(tratamiento);
                tratamientosCreados++;
            }
        }
        
        logger.info("Total de {} tratamientos creados", tratamientosCreados);
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
    
    /**
     * Crea plantillas de correo predeterminadas en la base de datos
     */
    private void crearPlantillasCorreo() {
        logger.info("Creando plantillas de correo predeterminadas...");
        
        if (plantillaCorreoRepository.count() > 0) {
            logger.info("Ya existen plantillas de correo. Saltando creación.");
            return;
        }
        
        // Plantilla 1: Bienvenida
        PlantillaCorreo bienvenida = new PlantillaCorreo(
            "Bienvenida al Sistema",
            "Bienvenido a AgroSoft - Sistema de Gestión Agropecuaria",
            "Estimado usuario,\n\n" +
            "Le damos la bienvenida al sistema AgroSoft, su plataforma integral para la gestión de su finca agropecuaria.\n\n" +
            "Con AgroSoft podrá:\n" +
            "• Gestionar su ganado y cultivos\n" +
            "• Registrar tratamientos veterinarios\n" +
            "• Realizar seguimiento de actividades\n" +
            "• Generar reportes y estadísticas\n\n" +
            "Si tiene alguna pregunta, no dude en contactarnos.\n\n" +
            "Saludos cordiales,\n" +
            "Equipo AgroSoft",
            "Bienvenida"
        );
        plantillaCorreoRepository.save(bienvenida);
        logger.info("Plantilla creada: Bienvenida al Sistema");
        
        // Plantilla 2: Notificación de Tratamiento
        PlantillaCorreo tratamiento = new PlantillaCorreo(
            "Notificación de Tratamiento Veterinario",
            "Recordatorio: Tratamiento Veterinario Programado",
            "Estimado usuario,\n\n" +
            "Le informamos que tiene un tratamiento veterinario programado para su ganado.\n\n" +
            "Detalles:\n" +
            "• Tipo de tratamiento: [Tipo]\n" +
            "• Fecha programada: [Fecha]\n" +
            "• Veterinario responsable: [Veterinario]\n\n" +
            "Por favor, asegúrese de estar presente en la fecha indicada o contacte con el veterinario para reprogramar si es necesario.\n\n" +
            "Saludos,\n" +
            "Sistema AgroSoft",
            "Notificación"
        );
        plantillaCorreoRepository.save(tratamiento);
        logger.info("Plantilla creada: Notificación de Tratamiento");
        
        // Plantilla 3: Alerta de Salud
        PlantillaCorreo alerta = new PlantillaCorreo(
            "Alerta de Salud del Ganado",
            "🚨 Alerta: Atención Requerida en el Ganado",
            "Estimado usuario,\n\n" +
            "Se ha detectado una situación que requiere su atención inmediata:\n\n" +
            "• Animal afectado: [Tipo y Raza]\n" +
            "• Estado de salud: [Estado]\n" +
            "• Observaciones: [Detalles]\n\n" +
            "Por favor, revise el sistema y tome las medidas necesarias. Si requiere asistencia veterinaria, contacte con su veterinario de confianza.\n\n" +
            "Saludos,\n" +
            "Sistema AgroSoft",
            "Alerta"
        );
        plantillaCorreoRepository.save(alerta);
        logger.info("Plantilla creada: Alerta de Salud");
        
        // Plantilla 4: Informe Mensual
        PlantillaCorreo informe = new PlantillaCorreo(
            "Informe Mensual de Actividades",
            "📊 Informe Mensual - Resumen de Actividades",
            "Estimado usuario,\n\n" +
            "Le presentamos el resumen mensual de actividades en su finca:\n\n" +
            "• Total de animales: [Cantidad]\n" +
            "• Tratamientos realizados: [Cantidad]\n" +
            "• Cultivos activos: [Cantidad]\n" +
            "• Actividades completadas: [Cantidad]\n\n" +
            "Puede acceder al sistema para ver más detalles y generar reportes completos.\n\n" +
            "Saludos,\n" +
            "Sistema AgroSoft",
            "Informe"
        );
        plantillaCorreoRepository.save(informe);
        logger.info("Plantilla creada: Informe Mensual");
        
        // Plantilla 5: Recordatorio de Vacunación
        PlantillaCorreo vacunacion = new PlantillaCorreo(
            "Recordatorio de Vacunación",
            "💉 Recordatorio: Vacunación Pendiente",
            "Estimado usuario,\n\n" +
            "Le recordamos que tiene vacunaciones pendientes para su ganado:\n\n" +
            "• Animal: [Tipo y Raza]\n" +
            "• Vacuna requerida: [Tipo de vacuna]\n" +
            "• Fecha recomendada: [Fecha]\n\n" +
            "Es importante mantener al día el calendario de vacunaciones para garantizar la salud de su ganado.\n\n" +
            "Saludos,\n" +
            "Sistema AgroSoft",
            "Recordatorio"
        );
        plantillaCorreoRepository.save(vacunacion);
        logger.info("Plantilla creada: Recordatorio de Vacunación");
        
        // Plantilla 6: Notificación de Nueva Actividad
        PlantillaCorreo actividad = new PlantillaCorreo(
            "Nueva Actividad Asignada",
            "📋 Nueva Actividad Asignada en AgroSoft",
            "Estimado usuario,\n\n" +
            "Se le ha asignado una nueva actividad en el sistema:\n\n" +
            "• Tipo de actividad: [Tipo]\n" +
            "• Descripción: [Descripción]\n" +
            "• Fecha programada: [Fecha]\n" +
            "• Prioridad: [Prioridad]\n\n" +
            "Por favor, revise los detalles en el sistema y confirme su disponibilidad.\n\n" +
            "Saludos,\n" +
            "Sistema AgroSoft",
            "Notificación"
        );
        plantillaCorreoRepository.save(actividad);
        logger.info("Plantilla creada: Nueva Actividad");
        
        logger.info("Plantillas de correo creadas correctamente. Total: {}", plantillaCorreoRepository.count());
    }
    
    /**
     * Método público para ejecutar la carga de datos desde el controlador
     * Este método puede ser llamado manualmente desde la interfaz web
     */
    @Transactional
    public void ejecutarCargaDatos() {
        logger.info("=== Ejecutando carga de datos desde la interfaz web ===");
        
        try {
            // 1. Crear roles si no existen
            crearRoles();
            
            // 2. Crear usuarios por defecto
            crearUsuariosIniciales();
            
            // 3. Crear pacientes (ganado) de ejemplo
            crearPacientesEjemplo();
            
            // 4. Crear tratamientos de ejemplo
            crearTratamientosEjemplo();
            
            // 5. Crear plantillas de correo predeterminadas
            crearPlantillasCorreo();
            
            logger.info("=== Carga de datos desde interfaz completada exitosamente ===");
        } catch (Exception e) {
            logger.error("Error durante la carga de datos desde interfaz: {}", e.getMessage(), e);
            throw e;
        }
    }
}
