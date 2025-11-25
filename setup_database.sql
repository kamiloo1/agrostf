-- -------------------------------
-- AgroSoft - Script completo MySQL
-- Fecha: 2025-11-03
-- Engine: InnoDB, charset utf8mb4
-- ACTUALIZADO: 
--   - Incluye campo numero_documento en usuarios
--   - Incluye todas las columnas de cultivos (tipo, area, estado, etc.)
--   - Incluye tablas: ganado, actividades, tratamientos, paciente
--   - Script unificado y completo - NO requiere fix_database.sql
-- -------------------------------

-- Elimina DB si existe (útil para pruebas)
DROP DATABASE IF EXISTS agrosft;

CREATE DATABASE agrosft CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE agrosft;

-- ------------------------------------------------
-- TABLA: roles
-- ------------------------------------------------
DROP TABLE IF EXISTS roles;

CREATE TABLE roles (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL UNIQUE, -- e.g. ADMIN, VETERINARIO, TRABAJADOR
  descripcion VARCHAR(255),
  creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ------------------------------------------------
-- TABLA: usuarios (ACTUALIZADA con numero_documento)
-- ------------------------------------------------
DROP TABLE IF EXISTS usuarios;

CREATE TABLE usuarios (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(120) NULL, -- Nullable para nuevos registros
  correo VARCHAR(150) NOT NULL UNIQUE,
  telefono VARCHAR(30),
  numero_documento VARCHAR(50) UNIQUE,
  password VARCHAR(255) NOT NULL, -- almacenar hash en producción
  role_id INT NULL, -- NULL para nuevos registros, asignado por admin
  activo TINYINT(1) DEFAULT 1,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  ultimo_login TIMESTAMP NULL,
  FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE RESTRICT ON UPDATE CASCADE,
  INDEX idx_usuarios_correo (correo),
  INDEX idx_usuarios_numero_documento (numero_documento),
  INDEX idx_usuarios_role (role_id)
) ENGINE=InnoDB;

-- ------------------------------------------------
-- TABLA: fincas (opcional: para multi-farm)
-- ------------------------------------------------
DROP TABLE IF EXISTS fincas;

CREATE TABLE fincas (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(150) NOT NULL,
  ubicacion VARCHAR(255),
  descripcion TEXT,
  creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ------------------------------------------------
-- TABLA: cultivos
-- ------------------------------------------------
DROP TABLE IF EXISTS cultivos;

CREATE TABLE cultivos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(150) NOT NULL,
  descripcion TEXT,
  tipo VARCHAR(100) NULL,
  area VARCHAR(100) NULL,
  estado VARCHAR(50) DEFAULT 'Activo',
  fecha_siembra DATE NULL,
  fecha_cosecha DATE NULL,
  observaciones TEXT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_actualizacion TIMESTAMP NULL,
  INDEX idx_cultivos_nombre (nombre),
  INDEX idx_cultivos_estado (estado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------
-- TABLA: siembras (registro de siembras)
-- ------------------------------------------------
DROP TABLE IF EXISTS siembras;

CREATE TABLE siembras (
  id INT AUTO_INCREMENT PRIMARY KEY,
  cultivo_id INT NOT NULL,
  finca_id INT NULL,
  trabajador_id INT NULL, -- usuario asignado que realizó la siembra
  fecha_siembra DATE NOT NULL,
  area DECIMAL(10,2) DEFAULT NULL, -- hectareas o m2 según convención
  estado ENUM('PLANIFICADA','EN_CURSO','FINALIZADA','CANCELADA') DEFAULT 'PLANIFICADA',
  observaciones TEXT,
  creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (cultivo_id) REFERENCES cultivos(id) ON DELETE RESTRICT ON UPDATE CASCADE,
  FOREIGN KEY (finca_id) REFERENCES fincas(id) ON DELETE SET NULL,
  FOREIGN KEY (trabajador_id) REFERENCES usuarios(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ------------------------------------------------
-- TABLA: abonos
-- ------------------------------------------------
DROP TABLE IF EXISTS abonos;

CREATE TABLE abonos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  siembra_id INT NULL,
  tipo_abono VARCHAR(120) NOT NULL,
  cantidad DECIMAL(10,2), -- unidades convenidas
  fecha_programada DATE NOT NULL,
  fecha_aplicacion DATE DEFAULT NULL,
  aplicado_por INT NULL,
  estado ENUM('PROGRAMADO','APLICADO','CANCELADO') DEFAULT 'PROGRAMADO',
  observaciones TEXT,
  creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (siembra_id) REFERENCES siembras(id) ON DELETE SET NULL,
  FOREIGN KEY (aplicado_por) REFERENCES usuarios(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ------------------------------------------------
-- TABLA: podas
-- ------------------------------------------------
DROP TABLE IF EXISTS podas;

CREATE TABLE podas (
  id INT AUTO_INCREMENT PRIMARY KEY,
  siembra_id INT NULL,
  tipo_poda VARCHAR(120) NOT NULL,
  fecha_programada DATE NOT NULL,
  fecha_realizada DATE DEFAULT NULL,
  realizada_por INT NULL,
  estado ENUM('PROGRAMADA','REALIZADA','CANCELADA') DEFAULT 'PROGRAMADA',
    observaciones TEXT,
  creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (siembra_id) REFERENCES siembras(id) ON DELETE SET NULL,
  FOREIGN KEY (realizada_por) REFERENCES usuarios(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ------------------------------------------------
-- TABLA: plagas
-- ------------------------------------------------
DROP TABLE IF EXISTS plagas;

CREATE TABLE plagas (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(150) NOT NULL,
  descripcion TEXT,
  severidad ENUM('BAJA','MEDIA','ALTA') DEFAULT 'MEDIA',
  creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ------------------------------------------------
-- TABLA: detecciones_plagas (reportes hechos por trabajadores)
-- ------------------------------------------------
DROP TABLE IF EXISTS detecciones_plagas;

CREATE TABLE detecciones_plagas (
  id INT AUTO_INCREMENT PRIMARY KEY,
  plaga_id INT NULL,
  siembra_id INT NULL,
  reportado_por INT NOT NULL,
  fecha_reporte DATETIME DEFAULT CURRENT_TIMESTAMP,
  caracteristicas TEXT, -- texto libre sobre síntomas
  estado ENUM('PENDIENTE','EN_REVISION','RESUELTO') DEFAULT 'PENDIENTE',
  imagen_url VARCHAR(255),
  acciones_tomadas TEXT,
  FOREIGN KEY (plaga_id) REFERENCES plagas(id) ON DELETE SET NULL,
  FOREIGN KEY (siembra_id) REFERENCES siembras(id) ON DELETE SET NULL,
  FOREIGN KEY (reportado_por) REFERENCES usuarios(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ------------------------------------------------
-- TABLA: riegos
-- ------------------------------------------------
DROP TABLE IF EXISTS riegos;

CREATE TABLE riegos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  siembra_id INT NULL,
  fecha_programada DATE NOT NULL,
  fecha_realizada DATE DEFAULT NULL,
  cantidad_agua_litros DECIMAL(12,2) DEFAULT NULL,
  realizado_por INT NULL,
  estado ENUM('PROGRAMADO','REALIZADO','SUSPENDIDO') DEFAULT 'PROGRAMADO',
    observaciones TEXT,
  creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (siembra_id) REFERENCES siembras(id) ON DELETE SET NULL,
  FOREIGN KEY (realizado_por) REFERENCES usuarios(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ------------------------------------------------
-- TABLA: huertas (para registros de huertas independientes)
-- ------------------------------------------------
DROP TABLE IF EXISTS huertas;

CREATE TABLE huertas (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(150) NOT NULL,
  ubicacion VARCHAR(255),
  responsable_id INT NULL,
  descripcion TEXT,
  creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  
  FOREIGN KEY (responsable_id) REFERENCES usuarios(id) ON DELETE SET NULL ON UPDATE CASCADE,
  INDEX idx_huertas_responsable (responsable_id)
) ENGINE=InnoDB;

-- ------------------------------------------------
-- TABLA: observaciones_huerta
-- ------------------------------------------------
DROP TABLE IF EXISTS observaciones_huerta;

CREATE TABLE observaciones_huerta (
  id INT AUTO_INCREMENT PRIMARY KEY,
  huerta_id INT NOT NULL,
  registrado_por INT NULL,
  fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
  comentario TEXT,
  fotos_url VARCHAR(255),
  FOREIGN KEY (huerta_id) REFERENCES huertas(id) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (registrado_por) REFERENCES usuarios(id) ON DELETE SET NULL ON UPDATE CASCADE,
  INDEX idx_observaciones_huerta_id (huerta_id),
  INDEX idx_observaciones_registrado_por (registrado_por)
) ENGINE=InnoDB;

-- ------------------------------------------------
-- TABLA: animales (ganado)
-- ------------------------------------------------
DROP TABLE IF EXISTS animales;

CREATE TABLE animales (
  id INT AUTO_INCREMENT PRIMARY KEY,
  codigo_identificacion VARCHAR(80) UNIQUE,
  tipo_animal VARCHAR(80) NOT NULL, -- e.g. VACA, CERDO
  raza VARCHAR(120),
  fecha_nacimiento DATE,
  estado_salud ENUM('SALUDABLE','EN_TRATAMIENTO','BAJA') DEFAULT 'SALUDABLE',
  responsable_id INT NULL,
  creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (responsable_id) REFERENCES usuarios(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ------------------------------------------------
-- TABLA: historial_medico (para animales)
-- ------------------------------------------------
DROP TABLE IF EXISTS historial_medico;

CREATE TABLE historial_medico (
  id INT AUTO_INCREMENT PRIMARY KEY,
  animal_id INT NOT NULL,
  fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
  veterinario_id INT NULL,
  descripcion TEXT,
  tratamiento TEXT,
  vacunado TINYINT(1) DEFAULT 0,
  dosis VARCHAR(100),
  proxima_visita DATE DEFAULT NULL,
  FOREIGN KEY (animal_id) REFERENCES animales(id) ON DELETE CASCADE,
  FOREIGN KEY (veterinario_id) REFERENCES usuarios(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ------------------------------------------------
-- TABLA: ganado (para compatibilidad con el código Java)
-- ------------------------------------------------
DROP TABLE IF EXISTS ganado;

CREATE TABLE ganado (
  id_ganado BIGINT AUTO_INCREMENT PRIMARY KEY,
  tipo VARCHAR(80) NOT NULL,
  raza VARCHAR(120),
  edad INT,
  peso DECIMAL(10,2),
  estado_salud VARCHAR(50),
  fecha_nacimiento DATE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  activo BOOLEAN DEFAULT TRUE,
  INDEX idx_ganado_tipo (tipo),
  INDEX idx_ganado_estado (estado_salud),
  INDEX idx_ganado_activo (activo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------
-- TABLA: actividades
-- ------------------------------------------------
DROP TABLE IF EXISTS actividades;

CREATE TABLE actividades (
  id_actividad BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_cultivo INT NULL,
  tipo_actividad VARCHAR(150) NOT NULL,
  descripcion TEXT,
  fecha_actividad DATE NOT NULL,
  trabajador_responsable VARCHAR(255),
  estado VARCHAR(50) DEFAULT 'PENDIENTE',
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_cultivo) REFERENCES cultivos(id) ON DELETE SET NULL ON UPDATE CASCADE,
  INDEX idx_actividades_cultivo (id_cultivo),
  INDEX idx_actividades_fecha (fecha_actividad)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------
-- TABLA: tratamientos
-- ------------------------------------------------
DROP TABLE IF EXISTS tratamientos;

CREATE TABLE tratamientos (
  id_tratamiento BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_ganado BIGINT NULL,
  tipo_tratamiento VARCHAR(150) NOT NULL,
  fecha_tratamiento DATE NOT NULL,
  observaciones TEXT,
  veterinario_responsable VARCHAR(255),
  costo DECIMAL(10,2),
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_ganado) REFERENCES ganado(id_ganado) ON DELETE SET NULL ON UPDATE CASCADE,
  INDEX idx_tratamientos_ganado (id_ganado),
  INDEX idx_tratamientos_fecha (fecha_tratamiento)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------
-- TABLA: paciente (para compatibilidad con el código Java)
-- ------------------------------------------------
DROP TABLE IF EXISTS paciente;

CREATE TABLE paciente (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(150) NOT NULL,
  especie VARCHAR(100),
  raza VARCHAR(120),
  edad VARCHAR(50),
  estado VARCHAR(50),
  observaciones TEXT,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------
-- TABLA: tareas (genérica para asignaciones)
-- ------------------------------------------------
DROP TABLE IF EXISTS tareas;

CREATE TABLE tareas (
  id INT AUTO_INCREMENT PRIMARY KEY,
  titulo VARCHAR(150) NOT NULL,
  descripcion TEXT,
  tipo ENUM('ABONO','PODA','RIEGO','PLAGA','SIEMBRA','GENERICA') DEFAULT 'GENERICA',
  asignado_a INT NULL,
  asignado_por INT NULL,
  fecha_inicio DATETIME DEFAULT NULL,
  fecha_vencimiento DATETIME DEFAULT NULL,
  estado ENUM('PENDIENTE','EN_PROGRESO','COMPLETADA','CANCELADA') DEFAULT 'PENDIENTE',
  creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (asignado_a) REFERENCES usuarios(id) ON DELETE SET NULL,
  FOREIGN KEY (asignado_por) REFERENCES usuarios(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ------------------------------------------------
-- Indices recomendados
-- NOTA: Algunos índices ya están definidos dentro de las tablas (ganado, actividades, tratamientos)
-- Solo se crean aquí los índices que no están en la definición de las tablas
-- ------------------------------------------------
CREATE INDEX idx_siembras_cultivo ON siembras(cultivo_id);
CREATE INDEX idx_abonos_siembra ON abonos(siembra_id);
CREATE INDEX idx_podas_siembra ON podas(siembra_id);
CREATE INDEX idx_detecciones_plagas_siembra ON detecciones_plagas(siembra_id);
CREATE INDEX idx_riegos_siembra ON riegos(siembra_id);
CREATE INDEX idx_animales_responsable ON animales(responsable_id);
-- idx_actividades_cultivo ya está definido en la tabla actividades
-- idx_tratamientos_ganado ya está definido en la tabla tratamientos

-- ------------------------------------------------
-- DATOS DE EJEMPLO: Roles y Usuarios
-- ------------------------------------------------
INSERT INTO roles (nombre, descripcion) VALUES
('ADMIN','Administrador con todos los permisos'),
('VETERINARIO','Usuario con permisos veterinarios'),
('TRABAJADOR','Trabajador agrícola');

-- Usuarios ejemplo (passwords en texto plano para pruebas: admin123, vet123, trab123)
-- NOTA: Estos usuarios tienen roles asignados porque son datos iniciales
-- Los nuevos usuarios registrados tendrán role_id NULL hasta que el administrador los asigne
INSERT INTO usuarios (nombre, correo, telefono, numero_documento, password, role_id)
VALUES
('Admin Principal','admin@agrosoft.local','+57-3000000000','1234567890','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', (SELECT id FROM roles WHERE nombre='ADMIN')),
('Dra. Marta','marta.vet@agrosoft.local','+57-3111111111','2345678901','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', (SELECT id FROM roles WHERE nombre='VETERINARIO')),
('Juan Obrero','juan.trab@agrosoft.local','+57-3222222222','3456789012','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', (SELECT id FROM roles WHERE nombre='TRABAJADOR'));

-- ------------------------------------------------
-- DATOS DE EJEMPLO: fincas, cultivos, siembras
-- ------------------------------------------------
INSERT INTO fincas (nombre, ubicacion, descripcion) VALUES
('Finca La Esperanza','Vereda El Bosque, Cundinamarca','Finca de ensayo'),
('Finca El Prado','Municipio X','Finca principal');

INSERT INTO cultivos (nombre, descripcion) VALUES
('Tomate','Tomate de mesa'),
('Maíz','Maíz de grano');

INSERT INTO siembras (cultivo_id, finca_id, trabajador_id, fecha_siembra, area, estado, observaciones)
VALUES
((SELECT id FROM cultivos WHERE nombre='Tomate'), (SELECT id FROM fincas WHERE nombre='Finca La Esperanza'), (SELECT id FROM usuarios WHERE nombre='Juan Obrero'),'2025-09-15', 1.25, 'EN_CURSO','Siembra experimental de 1.25 ha'),
((SELECT id FROM cultivos WHERE nombre='Maíz'), (SELECT id FROM fincas WHERE nombre='Finca El Prado'), NULL,'2025-08-01', 2.50, 'PLANIFICADA','Siembra en preparación');

-- ------------------------------------------------
-- DATOS DE EJEMPLO: Abonos, Podas, Plagas, Detecciones, Riegos
-- ------------------------------------------------
INSERT INTO abonos (siembra_id, tipo_abono, cantidad, fecha_programada, estado)
VALUES
((SELECT id FROM siembras WHERE area=1.25),'Fertilizante NPK 20-20-20', 25.0, '2025-10-01','PROGRAMADO'),
((SELECT id FROM siembras WHERE area=2.50),'Compost', 100.0, '2025-09-05','PROGRAMADO');

INSERT INTO podas (siembra_id, tipo_poda, fecha_programada, estado)
VALUES
((SELECT id FROM siembras WHERE area=1.25),'Poda formativa','2025-10-10','PROGRAMADA');

INSERT INTO plagas (nombre, descripcion, severidad) VALUES
('Pulgón','Pequeño insecto chupador que debilita la planta','MEDIA'),
('Trips','Insecto que produce manchas y enflaquecimiento','ALTA');

INSERT INTO detecciones_plagas (plaga_id, siembra_id, reportado_por, caracteristicas, imagen_url, acciones_tomadas)
VALUES
((SELECT id FROM plagas WHERE nombre='Pulgón'), (SELECT id FROM siembras WHERE area=1.25), (SELECT id FROM usuarios WHERE nombre='Juan Obrero'), 'Hojas con melaza y presencia de pequeños insectos', NULL, 'Aplicar jabón potásico, monitorear 7 días');

INSERT INTO riegos (siembra_id, fecha_programada, cantidad_agua_litros, estado)
VALUES
((SELECT id FROM siembras WHERE area=1.25),'2025-10-02', 1500.0, 'PROGRAMADO');

-- ------------------------------------------------
-- DATOS DE EJEMPLO: Huertas y Observaciones
-- ------------------------------------------------
INSERT INTO huertas (nombre, ubicacion, responsable_id, descripcion)
VALUES
('Huerta Comunitaria 1','Predio laboratorio', (SELECT id FROM usuarios WHERE nombre='Juan Obrero'), 'Huerta de prueba');

INSERT INTO observaciones_huerta (huerta_id, registrado_por, comentario)
VALUES
((SELECT id FROM huertas WHERE nombre='Huerta Comunitaria 1'), (SELECT id FROM usuarios WHERE nombre='Juan Obrero'), 'Se observó crecimiento desigual en la zona norte. Requiere abonado.');

-- ------------------------------------------------
-- DATOS DE EJEMPLO: Animales y Historial Médico
-- ------------------------------------------------
INSERT INTO animales (codigo_identificacion, tipo_animal, raza, fecha_nacimiento, estado_salud, responsable_id)
VALUES
('VAC-001','VACA','Holstein','2022-03-10','SALUDABLE', (SELECT id FROM usuarios WHERE nombre='Dra. Marta')),
('VAC-002','VACA','Jersey','2023-01-22','EN_TRATAMIENTO', (SELECT id FROM usuarios WHERE nombre='Dra. Marta'));

INSERT INTO historial_medico (animal_id, veterinario_id, descripcion, tratamiento, vacunado, dosis, proxima_visita)
VALUES
((SELECT id FROM animales WHERE codigo_identificacion='VAC-002'), (SELECT id FROM usuarios WHERE nombre='Dra. Marta'), 'Tratamiento por mastitis inicial', 'Antibiótico por 7 días', 1, '10ml', '2025-11-15');

-- ------------------------------------------------
-- DATOS DE EJEMPLO: Tareas
-- ------------------------------------------------
INSERT INTO tareas (titulo, descripcion, tipo, asignado_a, asignado_por, fecha_inicio, fecha_vencimiento, estado)
VALUES
('Aplicar abono en parcela A','Aplicar 25kg NPK 20-20-20 en parcela A','ABONO', (SELECT id FROM usuarios WHERE nombre='Juan Obrero'), (SELECT id FROM usuarios WHERE nombre='Admin Principal'), '2025-10-01 08:00:00', '2025-10-01 17:00:00', 'PENDIENTE'),
('Inspección de plagas - parcela A','Revisar presencia de pulgón y tomar fotos','PLAGA', (SELECT id FROM usuarios WHERE nombre='Juan Obrero'), (SELECT id FROM usuarios WHERE nombre='Admin Principal'), NULL, '2025-10-03 12:00:00', 'PENDIENTE');

-- ------------------------------------------------
-- Mensajes finales
-- ------------------------------------------------
SELECT 'Base de datos agrosft configurada exitosamente' AS mensaje;
SELECT COUNT(*) AS total_usuarios FROM usuarios;
SELECT COUNT(*) AS total_roles FROM roles;
SELECT COUNT(*) AS total_cultivos FROM cultivos;
