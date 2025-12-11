# 📋 GUÍA DE USO - CARGA INICIAL DE DATOS AGROSOFT

## 🎯 ¿Qué es este archivo?

El archivo `carga-inicial-datos_.sql` contiene:
- ✅ Estructura completa de todas las tablas
- ✅ Datos iniciales (223+ registros)
- ✅ Contraseñas encriptadas con BCrypt
- ✅ Relaciones entre tablas correctamente establecidas

## 🚀 CÓMO USARLO

### Opción 1: Desde MySQL Workbench (Recomendado)

1. **Abrir MySQL Workbench**
2. **Conectar a tu servidor MySQL** (localhost:3306)
3. **Crear o seleccionar la base de datos:**
   ```sql
   CREATE DATABASE IF NOT EXISTS agrosft;
   USE agrosft;
   ```
4. **Ejecutar el script:**
   - Click en `File` → `Run SQL Script...`
   - Seleccionar el archivo `carga-inicial-datos_.sql`
   - Click en `Run` o presionar `Ctrl+Shift+Enter`
5. **Verificar que se ejecutó correctamente:**
   - Al final verás una tabla con el conteo de registros por tabla

### Opción 2: Desde Línea de Comandos (Terminal/CMD)

```bash
# Windows (PowerShell o CMD)
mysql -u root -p agrosft < carga-inicial-datos_.sql

# Linux/Mac
mysql -u root -p agrosft < carga-inicial-datos_.sql
```

**Nota:** Te pedirá la contraseña de MySQL. Si no tienes contraseña, presiona Enter.

### Opción 3: Desde Consola MySQL

```sql
-- Conectar a MySQL
mysql -u root -p

-- Seleccionar base de datos
USE agrosft;

-- Ejecutar el script
source C:/Users/camilo/Desktop/agrosoft-crud1/carga-inicial-datos_.sql;
-- O la ruta completa donde tengas el archivo
```

## ✅ CÓMO VERIFICAR QUE FUNCIONÓ

### 1. Verificar que las tablas se crearon

```sql
USE agrosft;
SHOW TABLES;
```

**Deberías ver:** roles, usuarios, fincas, cultivos, siembras, ganado, tratamientos, actividades, plagas, detecciones_plagas, abonos, riegos, podas, huertas, observaciones_huerta, tareas

### 2. Verificar cantidad de registros

```sql
-- Ver conteo de registros por tabla
SELECT 'Roles' AS tabla, COUNT(*) AS total FROM roles
UNION ALL SELECT 'Usuarios', COUNT(*) FROM usuarios
UNION ALL SELECT 'Fincas', COUNT(*) FROM fincas
UNION ALL SELECT 'Cultivos', COUNT(*) FROM cultivos
UNION ALL SELECT 'Siembras', COUNT(*) FROM siembras
UNION ALL SELECT 'Ganado', COUNT(*) FROM ganado
UNION ALL SELECT 'Tratamientos', COUNT(*) FROM tratamientos
UNION ALL SELECT 'Actividades', COUNT(*) FROM actividades
UNION ALL SELECT 'Plagas', COUNT(*) FROM plagas
UNION ALL SELECT 'Detecciones Plagas', COUNT(*) FROM detecciones_plagas
UNION ALL SELECT 'Abonos', COUNT(*) FROM abonos
UNION ALL SELECT 'Riegos', COUNT(*) FROM riegos
UNION ALL SELECT 'Podas', COUNT(*) FROM podas
UNION ALL SELECT 'Huertas', COUNT(*) FROM huertas
UNION ALL SELECT 'Observaciones Huerta', COUNT(*) FROM observaciones_huerta
UNION ALL SELECT 'Tareas', COUNT(*) FROM tareas;
```

**Resultado esperado:**
- Roles: 3
- Usuarios: 20
- Fincas: 5
- Cultivos: 25
- Siembras: 15
- Ganado: 30
- Tratamientos: 25
- Actividades: 20
- Plagas: 10
- Detecciones Plagas: 15
- Abonos: 12
- Riegos: 15
- Podas: 10
- Huertas: 5
- Observaciones Huerta: 12
- Tareas: 20

### 3. Verificar usuarios y contraseñas

```sql
-- Ver usuarios creados
SELECT u.id, u.nombre, u.correo, r.nombre AS rol, u.activo 
FROM usuarios u 
LEFT JOIN roles r ON u.role_id = r.id
ORDER BY r.nombre, u.nombre;
```

**Usuarios de prueba:**
- `admin@agrosoft.local` / `admin123` (ADMIN)
- `veterinario@agrosoft.local` / `vet123` (VETERINARIO)
- `trabajador@agrosoft.local` / `trab123` (TRABAJADOR)

### 4. Verificar relaciones entre tablas

```sql
-- Ver siembras con sus cultivos y fincas
SELECT s.id, c.nombre AS cultivo, f.nombre AS finca, s.area, s.estado
FROM siembras s
LEFT JOIN cultivos c ON s.cultivo_id = c.id
LEFT JOIN fincas f ON s.finca_id = f.id
LIMIT 10;

-- Ver tratamientos con ganado
SELECT t.id_tratamiento, g.tipo AS animal, g.raza, t.tipo_tratamiento, t.fecha_tratamiento, t.costo
FROM tratamientos t
LEFT JOIN ganado g ON t.id_ganado = g.id_ganado
LIMIT 10;

-- Ver actividades con cultivos
SELECT a.id_actividad, c.nombre AS cultivo, a.tipo_actividad, a.estado, a.fecha_actividad
FROM actividades a
LEFT JOIN cultivos c ON a.id_cultivo = c.id
LIMIT 10;
```

### 5. Probar en la aplicación

1. **Iniciar la aplicación Spring Boot:**
   ```bash
   mvn spring-boot:run
   ```

2. **Acceder a la aplicación:**
   - URL: `http://localhost:8085`
   - Login con: `admin@agrosoft.local` / `admin123`

3. **Verificar que los datos aparecen:**
   - Dashboard: Debería mostrar estadísticas
   - Lista de usuarios: Debería mostrar 20 usuarios
   - Lista de cultivos: Debería mostrar 25 cultivos
   - Lista de ganado: Debería mostrar 30 animales
   - Reportes PDF: Debería generar gráficas con datos

## 🧪 PRUEBAS ESPECÍFICAS

### Prueba 1: Login con usuarios creados

```sql
-- Verificar que los usuarios tienen contraseñas encriptadas
SELECT correo, password, 
       CASE 
         WHEN password LIKE '$2a$10$%' THEN 'BCrypt (Correcto)'
         ELSE 'Texto plano (Error)'
       END AS tipo_encriptacion
FROM usuarios
WHERE correo IN ('admin@agrosoft.local', 'veterinario@agrosoft.local', 'trabajador@agrosoft.local');
```

### Prueba 2: Verificar integridad referencial

```sql
-- Verificar que no hay registros huérfanos
SELECT 'Siembras sin cultivo' AS problema, COUNT(*) AS cantidad
FROM siembras WHERE cultivo_id NOT IN (SELECT id FROM cultivos)
UNION ALL
SELECT 'Tratamientos sin ganado', COUNT(*)
FROM tratamientos WHERE id_ganado NOT IN (SELECT id_ganado FROM ganado)
UNION ALL
SELECT 'Usuarios sin rol válido', COUNT(*)
FROM usuarios WHERE role_id IS NOT NULL AND role_id NOT IN (SELECT id FROM roles);
```

**Resultado esperado:** Todas las cantidades deben ser 0

### Prueba 3: Verificar datos de ganado

```sql
-- Ver distribución de ganado por tipo
SELECT tipo, COUNT(*) AS cantidad, 
       AVG(peso) AS peso_promedio,
       SUM(CASE WHEN estado_salud = 'Saludable' THEN 1 ELSE 0 END) AS saludables
FROM ganado
GROUP BY tipo
ORDER BY cantidad DESC;
```

### Prueba 4: Verificar actividades por estado

```sql
-- Ver distribución de actividades
SELECT estado, COUNT(*) AS cantidad
FROM actividades
GROUP BY estado
ORDER BY cantidad DESC;
```

## 📊 DEMOSTRACIÓN VISUAL

### Crear un reporte de verificación

```sql
-- Script completo de verificación
SELECT 
    '=== RESUMEN DE CARGA DE DATOS ===' AS seccion,
    '' AS detalle
UNION ALL
SELECT 'Total de Roles:', CAST(COUNT(*) AS CHAR) FROM roles
UNION ALL
SELECT 'Total de Usuarios:', CAST(COUNT(*) AS CHAR) FROM usuarios
UNION ALL
SELECT '  - Administradores:', CAST(COUNT(*) AS CHAR) FROM usuarios u JOIN roles r ON u.role_id = r.id WHERE r.nombre = 'ADMIN'
UNION ALL
SELECT '  - Veterinarios:', CAST(COUNT(*) AS CHAR) FROM usuarios u JOIN roles r ON u.role_id = r.id WHERE r.nombre = 'VETERINARIO'
UNION ALL
SELECT '  - Trabajadores:', CAST(COUNT(*) AS CHAR) FROM usuarios u JOIN roles r ON u.role_id = r.id WHERE r.nombre = 'TRABAJADOR'
UNION ALL
SELECT 'Total de Fincas:', CAST(COUNT(*) AS CHAR) FROM fincas
UNION ALL
SELECT 'Total de Cultivos:', CAST(COUNT(*) AS CHAR) FROM cultivos
UNION ALL
SELECT 'Total de Siembras:', CAST(COUNT(*) AS CHAR) FROM siembras
UNION ALL
SELECT 'Total de Ganado:', CAST(COUNT(*) AS CHAR) FROM ganado
UNION ALL
SELECT 'Total de Tratamientos:', CAST(COUNT(*) AS CHAR) FROM tratamientos
UNION ALL
SELECT 'Total de Actividades:', CAST(COUNT(*) AS CHAR) FROM actividades
UNION ALL
SELECT 'Total de Plagas:', CAST(COUNT(*) AS CHAR) FROM plagas
UNION ALL
SELECT 'Total de Detecciones:', CAST(COUNT(*) AS CHAR) FROM detecciones_plagas
UNION ALL
SELECT 'Total de Abonos:', CAST(COUNT(*) AS CHAR) FROM abonos
UNION ALL
SELECT 'Total de Riegos:', CAST(COUNT(*) AS CHAR) FROM riegos
UNION ALL
SELECT 'Total de Podas:', CAST(COUNT(*) AS CHAR) FROM podas
UNION ALL
SELECT 'Total de Huertas:', CAST(COUNT(*) AS CHAR) FROM huertas
UNION ALL
SELECT 'Total de Observaciones:', CAST(COUNT(*) AS CHAR) FROM observaciones_huerta
UNION ALL
SELECT 'Total de Tareas:', CAST(COUNT(*) AS CHAR) FROM tareas;
```

## ⚠️ SOLUCIÓN DE PROBLEMAS

### Error: "Table already exists"
**Solución:** El script usa `DROP TABLE IF EXISTS`, pero si quieres mantener datos existentes, comenta las líneas DROP.

### Error: "Foreign key constraint fails"
**Solución:** Asegúrate de ejecutar el script completo en orden. No ejecutes solo partes del script.

### Error: "Access denied"
**Solución:** Verifica que tienes permisos de root o un usuario con permisos completos.

### No aparecen datos en la aplicación
**Solución:** 
1. Verifica que la aplicación apunta a la base de datos correcta (`agrosft`)
2. Reinicia la aplicación Spring Boot
3. Verifica los logs de la aplicación

## 📝 NOTAS IMPORTANTES

1. **Contraseñas:** Todas están encriptadas con BCrypt. La contraseña por defecto es `admin123`, `vet123`, o `trab123` según el usuario.

2. **Datos de prueba:** Estos son datos de ejemplo. En producción, cambia las contraseñas.

3. **Relaciones:** Todas las relaciones están correctamente establecidas. Los datos están relacionados entre sí.

4. **Orden de ejecución:** El script está diseñado para ejecutarse completo. No ejecutes solo partes.

## 🎉 RESULTADO ESPERADO

Después de ejecutar el script correctamente:
- ✅ 16 tablas creadas
- ✅ 223+ registros insertados
- ✅ Todas las relaciones funcionando
- ✅ Aplicación lista para usar con datos de prueba

---

**¿Necesitas ayuda?** Revisa los logs de MySQL o los logs de la aplicación Spring Boot para más detalles.

