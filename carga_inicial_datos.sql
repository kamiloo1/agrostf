-- ============================================================
-- AGROSOFT - SCRIPT DE CARGA INICIAL DE DATOS
-- ============================================================
-- Este archivo contiene todos los datos iniciales que se cargan
-- automáticamente al iniciar la aplicación mediante DataInitializer.java
-- 
-- INSTRUCCIONES DE USO:
-- 1. Asegúrate de que la base de datos 'agrosft' esté creada
-- 2. Ejecuta este script en MySQL:
--    mysql -u root -p agrosft < carga_inicial_datos.sql
--    O desde MySQL Workbench: File > Run SQL Script
--
-- NOTA: Este script usa INSERT IGNORE para evitar duplicados
-- ============================================================

USE agrosft;

-- ============================================================
-- 1. ROLES DEL SISTEMA
-- ============================================================
-- Crear roles si no existen
INSERT IGNORE INTO roles (nombre, descripcion) VALUES
('ADMIN', 'Administrador con todos los permisos'),
('VETERINARIO', 'Usuario con permisos veterinarios'),
('TRABAJADOR', 'Trabajador agrícola');

-- ============================================================
-- 2. USUARIOS INICIALES
-- ============================================================
-- Contraseñas encriptadas con BCrypt (Spring Security)
-- Todas las contraseñas son: admin123, vet123, trab123 respectivamente
-- 
-- IMPORTANTE: Las contraseñas están encriptadas. Para cambiar:
-- 1. Usa un generador de BCrypt online
-- 2. O usa el PasswordEncoder de Spring en código Java

INSERT IGNORE INTO usuarios (nombre, correo, telefono, numero_documento, password, role_id, activo, fecha_creacion) VALUES
-- Administrador Principal
('Administrador Principal', 'admin@agrosoft.local', '+57-3000000000', '1234567890', 
 '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 
 (SELECT id FROM roles WHERE nombre='ADMIN'), 1, NOW()),

-- Veterinario
('Dr. Carlos Veterinario', 'veterinario@agrosoft.local', '+57-3111111111', '2345678901', 
 '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 
 (SELECT id FROM roles WHERE nombre='VETERINARIO'), 1, NOW()),

-- Trabajador
('Juan Trabajador', 'trabajador@agrosoft.local', '+57-3222222222', '3456789012', 
 '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 
 (SELECT id FROM roles WHERE nombre='TRABAJADOR'), 1, NOW());

-- ============================================================
-- 3. PACIENTES (GANADO) DE EJEMPLO
-- ============================================================
-- Crear pacientes de ejemplo solo si no existen registros

-- Verificar si ya hay ganado (esto se hace en la aplicación, aquí solo insertamos)
INSERT IGNORE INTO ganado (tipo, raza, edad, peso, estado_salud, fecha_nacimiento, activo, fecha_creacion) VALUES
-- Paciente 1: Vaca Holstein
('Vaca', 'Holstein', 5, 450.0, 'Saludable', '2019-03-15', 1, NOW()),

-- Paciente 2: Cerdo Yorkshire
('Cerdo', 'Yorkshire', 2, 120.0, 'En Tratamiento', '2022-06-20', 1, NOW()),

-- Paciente 3: Vaca Jersey
('Vaca', 'Jersey', 3, 380.0, 'Saludable', '2021-01-10', 1, NOW());

-- ============================================================
-- 4. TRATAMIENTOS DE EJEMPLO
-- ============================================================
-- Crear tratamientos asociados a los pacientes creados
-- NOTA: Los IDs de ganado pueden variar, por eso usamos subconsultas

INSERT IGNORE INTO tratamientos (
    id_ganado, 
    tipo_tratamiento, 
    fecha_tratamiento, 
    observaciones, 
    veterinario_responsable, 
    costo, 
    fecha_creacion
) VALUES
-- Tratamiento 1: Vacunación (hace 10 días)
(
    (SELECT id_ganado FROM ganado WHERE tipo='Vaca' AND raza='Holstein' LIMIT 1),
    'Vacunación',
    DATE_SUB(CURDATE(), INTERVAL 10 DAY),
    'Vacunación anual contra fiebre aftosa',
    'Dr. Carlos Veterinario',
    50000.00,
    NOW()
),

-- Tratamiento 2: Desparasitación (hace 5 días)
(
    (SELECT id_ganado FROM ganado WHERE tipo='Cerdo' AND raza='Yorkshire' LIMIT 1),
    'Desparasitación',
    DATE_SUB(CURDATE(), INTERVAL 5 DAY),
    'Tratamiento antiparasitario completo',
    'Dr. Carlos Veterinario',
    35000.00,
    NOW()
),

-- Tratamiento 3: Antibiótico (hoy - activo)
(
    (SELECT id_ganado FROM ganado WHERE tipo='Cerdo' AND raza='Yorkshire' LIMIT 1),
    'Antibiótico',
    CURDATE(),
    'Tratamiento por infección respiratoria',
    'Dr. Carlos Veterinario',
    75000.00,
    NOW()
);

-- ============================================================
-- VERIFICACIÓN DE DATOS INSERTADOS
-- ============================================================
-- Ejecuta estas consultas para verificar que los datos se insertaron correctamente

-- SELECT '=== ROLES ===' AS '';
-- SELECT * FROM roles;
-- 
-- SELECT '=== USUARIOS ===' AS '';
-- SELECT u.id, u.nombre, u.correo, u.telefono, r.nombre AS rol, u.activo 
-- FROM usuarios u 
-- LEFT JOIN roles r ON u.role_id = r.id;
-- 
-- SELECT '=== GANADO ===' AS '';
-- SELECT id_ganado, tipo, raza, edad, peso, estado_salud, activo 
-- FROM ganado;
-- 
-- SELECT '=== TRATAMIENTOS ===' AS '';
-- SELECT t.id_tratamiento, g.tipo AS animal, t.tipo_tratamiento, t.fecha_tratamiento, t.costo
-- FROM tratamientos t
-- LEFT JOIN ganado g ON t.id_ganado = g.id_ganado;

-- ============================================================
-- FIN DEL SCRIPT
-- ============================================================
-- Datos cargados:
-- - 3 Roles (ADMIN, VETERINARIO, TRABAJADOR)
-- - 3 Usuarios (admin, veterinario, trabajador)
-- - 3 Pacientes/Ganado (Vaca Holstein, Cerdo Yorkshire, Vaca Jersey)
-- - 3 Tratamientos (Vacunación, Desparasitación, Antibiótico)
-- ============================================================

