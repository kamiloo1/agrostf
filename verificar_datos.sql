-- ====================================================================================
-- SCRIPT DE VERIFICACIÓN - CARGA INICIAL DE DATOS AGROSOFT
-- ====================================================================================
-- Este script verifica que todos los datos se cargaron correctamente
-- Ejecuta este script después de cargar carga-inicial-datos_.sql
-- ====================================================================================

USE agrosft;

-- ====================================================================================
-- 1. VERIFICAR ESTRUCTURA DE TABLAS
-- ====================================================================================
SELECT '=== VERIFICACIÓN DE TABLAS ===' AS verificacion;
SHOW TABLES;

-- ====================================================================================
-- 2. VERIFICAR CANTIDAD DE REGISTROS POR TABLA
-- ====================================================================================
SELECT '=== CANTIDAD DE REGISTROS ===' AS verificacion;

SELECT 'Roles' AS tabla, COUNT(*) AS total, 'Esperado: 3' AS esperado FROM roles
UNION ALL
SELECT 'Usuarios', COUNT(*), 'Esperado: 20' FROM usuarios
UNION ALL
SELECT 'Fincas', COUNT(*), 'Esperado: 5' FROM fincas
UNION ALL
SELECT 'Cultivos', COUNT(*), 'Esperado: 25' FROM cultivos
UNION ALL
SELECT 'Siembras', COUNT(*), 'Esperado: 15' FROM siembras
UNION ALL
SELECT 'Ganado', COUNT(*), 'Esperado: 30' FROM ganado
UNION ALL
SELECT 'Tratamientos', COUNT(*), 'Esperado: 25' FROM tratamientos
UNION ALL
SELECT 'Actividades', COUNT(*), 'Esperado: 20' FROM actividades
UNION ALL
SELECT 'Plagas', COUNT(*), 'Esperado: 10' FROM plagas
UNION ALL
SELECT 'Detecciones Plagas', COUNT(*), 'Esperado: 15' FROM detecciones_plagas
UNION ALL
SELECT 'Abonos', COUNT(*), 'Esperado: 12' FROM abonos
UNION ALL
SELECT 'Riegos', COUNT(*), 'Esperado: 15' FROM riegos
UNION ALL
SELECT 'Podas', COUNT(*), 'Esperado: 10' FROM podas
UNION ALL
SELECT 'Huertas', COUNT(*), 'Esperado: 5' FROM huertas
UNION ALL
SELECT 'Observaciones Huerta', COUNT(*), 'Esperado: 12' FROM observaciones_huerta
UNION ALL
SELECT 'Tareas', COUNT(*), 'Esperado: 20' FROM tareas;

-- ====================================================================================
-- 3. VERIFICAR USUARIOS Y ROLES
-- ====================================================================================
SELECT '=== USUARIOS POR ROL ===' AS verificacion;

SELECT r.nombre AS rol, COUNT(u.id) AS cantidad_usuarios
FROM roles r
LEFT JOIN usuarios u ON r.id = u.role_id
GROUP BY r.id, r.nombre
ORDER BY r.nombre;

-- Ver usuarios principales
SELECT '=== USUARIOS PRINCIPALES ===' AS verificacion;
SELECT u.id, u.nombre, u.correo, r.nombre AS rol, 
       CASE 
         WHEN u.password LIKE '$2a$10$%' THEN '✓ BCrypt'
         ELSE '✗ Texto plano'
       END AS encriptacion,
       CASE WHEN u.activo = 1 THEN '✓ Activo' ELSE '✗ Inactivo' END AS estado
FROM usuarios u
LEFT JOIN roles r ON u.role_id = r.id
WHERE u.correo IN ('admin@agrosoft.local', 'veterinario@agrosoft.local', 'trabajador@agrosoft.local')
ORDER BY r.nombre;

-- ====================================================================================
-- 4. VERIFICAR INTEGRIDAD REFERENCIAL
-- ====================================================================================
SELECT '=== VERIFICACIÓN DE INTEGRIDAD REFERENCIAL ===' AS verificacion;

SELECT 'Siembras sin cultivo' AS problema, COUNT(*) AS cantidad
FROM siembras 
WHERE cultivo_id IS NOT NULL AND cultivo_id NOT IN (SELECT id FROM cultivos)
UNION ALL
SELECT 'Tratamientos sin ganado', COUNT(*)
FROM tratamientos 
WHERE id_ganado IS NOT NULL AND id_ganado NOT IN (SELECT id_ganado FROM ganado)
UNION ALL
SELECT 'Usuarios con rol inválido', COUNT(*)
FROM usuarios 
WHERE role_id IS NOT NULL AND role_id NOT IN (SELECT id FROM roles)
UNION ALL
SELECT 'Actividades sin cultivo válido', COUNT(*)
FROM actividades 
WHERE id_cultivo IS NOT NULL AND id_cultivo NOT IN (SELECT id FROM cultivos)
UNION ALL
SELECT 'Abonos sin siembra válida', COUNT(*)
FROM abonos 
WHERE siembra_id IS NOT NULL AND siembra_id NOT IN (SELECT id FROM siembras)
UNION ALL
SELECT 'Riegos sin siembra válida', COUNT(*)
FROM riegos 
WHERE siembra_id IS NOT NULL AND siembra_id NOT IN (SELECT id FROM siembras)
UNION ALL
SELECT 'Podas sin siembra válida', COUNT(*)
FROM podas 
WHERE siembra_id IS NOT NULL AND siembra_id NOT IN (SELECT id FROM siembras)
UNION ALL
SELECT 'Detecciones sin plaga válida', COUNT(*)
FROM detecciones_plagas 
WHERE plaga_id IS NOT NULL AND plaga_id NOT IN (SELECT id FROM plagas)
UNION ALL
SELECT 'Detecciones sin siembra válida', COUNT(*)
FROM detecciones_plagas 
WHERE siembra_id IS NOT NULL AND siembra_id NOT IN (SELECT id FROM siembras)
UNION ALL
SELECT 'Observaciones sin huerta válida', COUNT(*)
FROM observaciones_huerta 
WHERE huerta_id NOT IN (SELECT id FROM huertas);

-- Si todas las cantidades son 0, la integridad está correcta ✓

-- ====================================================================================
-- 5. VERIFICAR DATOS DE GANADO
-- ====================================================================================
SELECT '=== DISTRIBUCIÓN DE GANADO ===' AS verificacion;

SELECT tipo, 
       COUNT(*) AS cantidad,
       AVG(peso) AS peso_promedio_kg,
       SUM(CASE WHEN estado_salud = 'Saludable' THEN 1 ELSE 0 END) AS saludables,
       SUM(CASE WHEN estado_salud = 'En Tratamiento' THEN 1 ELSE 0 END) AS en_tratamiento,
       SUM(CASE WHEN estado_salud = 'En Observación' THEN 1 ELSE 0 END) AS en_observacion
FROM ganado
GROUP BY tipo
ORDER BY cantidad DESC;

-- ====================================================================================
-- 6. VERIFICAR TRATAMIENTOS
-- ====================================================================================
SELECT '=== TRATAMIENTOS POR TIPO ===' AS verificacion;

SELECT tipo_tratamiento, 
       COUNT(*) AS cantidad,
       AVG(costo) AS costo_promedio,
       MIN(fecha_tratamiento) AS fecha_mas_antigua,
       MAX(fecha_tratamiento) AS fecha_mas_reciente
FROM tratamientos
GROUP BY tipo_tratamiento
ORDER BY cantidad DESC;

-- ====================================================================================
-- 7. VERIFICAR ACTIVIDADES
-- ====================================================================================
SELECT '=== ACTIVIDADES POR ESTADO ===' AS verificacion;

SELECT estado, COUNT(*) AS cantidad
FROM actividades
GROUP BY estado
ORDER BY cantidad DESC;

SELECT '=== ACTIVIDADES POR TIPO ===' AS verificacion;

SELECT tipo_actividad, COUNT(*) AS cantidad
FROM actividades
GROUP BY tipo_actividad
ORDER BY cantidad DESC
LIMIT 10;

-- ====================================================================================
-- 8. VERIFICAR CULTIVOS Y SIEMBRAS
-- ====================================================================================
SELECT '=== CULTIVOS POR ESTADO ===' AS verificacion;

SELECT estado, COUNT(*) AS cantidad
FROM cultivos
GROUP BY estado
ORDER BY cantidad DESC;

SELECT '=== SIEMBRAS POR ESTADO ===' AS verificacion;

SELECT estado, COUNT(*) AS cantidad
FROM siembras
GROUP BY estado
ORDER BY cantidad DESC;

-- ====================================================================================
-- 9. VERIFICAR RELACIONES COMPLEJAS
-- ====================================================================================
SELECT '=== SIEMBRAS CON CULTIVOS Y FINCAS ===' AS verificacion;

SELECT s.id, 
       c.nombre AS cultivo, 
       f.nombre AS finca,
       s.area AS area_hectareas,
       s.estado,
       u.nombre AS trabajador
FROM siembras s
LEFT JOIN cultivos c ON s.cultivo_id = c.id
LEFT JOIN fincas f ON s.finca_id = f.id
LEFT JOIN usuarios u ON s.trabajador_id = u.id
LIMIT 10;

SELECT '=== TRATAMIENTOS CON GANADO ===' AS verificacion;

SELECT t.id_tratamiento,
       g.tipo AS animal,
       g.raza,
       t.tipo_tratamiento,
       t.fecha_tratamiento,
       t.costo,
       t.veterinario_responsable
FROM tratamientos t
LEFT JOIN ganado g ON t.id_ganado = g.id_ganado
ORDER BY t.fecha_tratamiento DESC
LIMIT 10;

-- ====================================================================================
-- 10. RESUMEN FINAL
-- ====================================================================================
SELECT '=== RESUMEN FINAL ===' AS verificacion;

SELECT 
    CONCAT('✓ Total de registros: ', 
           (SELECT COUNT(*) FROM roles) +
           (SELECT COUNT(*) FROM usuarios) +
           (SELECT COUNT(*) FROM fincas) +
           (SELECT COUNT(*) FROM cultivos) +
           (SELECT COUNT(*) FROM siembras) +
           (SELECT COUNT(*) FROM ganado) +
           (SELECT COUNT(*) FROM tratamientos) +
           (SELECT COUNT(*) FROM actividades) +
           (SELECT COUNT(*) FROM plagas) +
           (SELECT COUNT(*) FROM detecciones_plagas) +
           (SELECT COUNT(*) FROM abonos) +
           (SELECT COUNT(*) FROM riegos) +
           (SELECT COUNT(*) FROM podas) +
           (SELECT COUNT(*) FROM huertas) +
           (SELECT COUNT(*) FROM observaciones_huerta) +
           (SELECT COUNT(*) FROM tareas)
    ) AS resultado;

SELECT 
    CASE 
        WHEN (SELECT COUNT(*) FROM roles) = 3 
         AND (SELECT COUNT(*) FROM usuarios) = 20
         AND (SELECT COUNT(*) FROM ganado) = 30
         AND (SELECT COUNT(*) FROM tratamientos) = 25
        THEN '✓ CARGA DE DATOS EXITOSA'
        ELSE '✗ VERIFICAR CANTIDADES'
    END AS estado_final;

-- ====================================================================================
-- FIN DEL SCRIPT DE VERIFICACIÓN
-- ====================================================================================

