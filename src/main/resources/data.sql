-- Script de inicialización de datos para AgroSoft (agrosft)
-- NOTA: Este script solo se ejecuta si spring.sql.init.mode=always
-- Si ya ejecutaste database_setup_agrosft.sql, puedes dejar spring.sql.init.mode=never

-- Insertar roles si no existen
INSERT IGNORE INTO roles (nombre, descripcion) VALUES
('ADMIN','Administrador con todos los permisos'),
('VETERINARIO','Usuario con permisos veterinarios'),
('TRABAJADOR','Trabajador agrícola');

-- Insertar usuarios de prueba (solo si no existen)
-- NOTA: Estos usuarios tienen roles asignados porque son datos iniciales
-- Los nuevos usuarios registrados tendrán role_id NULL hasta que el administrador los asigne
INSERT IGNORE INTO usuarios (nombre, correo, telefono, numero_documento, password, role_id, activo) VALUES 
('Administrador Principal', 'admin@agrosoft.local', '+57-3000000000', '1234567890', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 
 (SELECT id FROM roles WHERE nombre='ADMIN'), 1),
('Dra. Marta', 'marta.vet@agrosoft.local', '+57-3111111111', '2345678901', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 
 (SELECT id FROM roles WHERE nombre='VETERINARIO'), 1),
('Juan Obrero', 'juan.trab@agrosoft.local', '+57-3222222222', '3456789012', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 
 (SELECT id FROM roles WHERE nombre='TRABAJADOR'), 1);

-- Insertar cultivos de ejemplo (solo si no existen)
INSERT IGNORE INTO cultivos (nombre, descripcion) VALUES 
('Maíz Híbrido', 'Maíz de grano'),
('Tomate Cherry', 'Tomate de mesa'),
('Lechuga Romana', 'Verdura fresca'),
('Cebolla Amarilla', 'Hortaliza'),
('Papa Criolla', 'Tubérculo');
