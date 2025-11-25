# ✅ Verificación Inicial del Proyecto AgroSoft

Este documento contiene los pasos para verificar que todo esté configurado correctamente antes de ejecutar el proyecto.

## 🔍 Verificaciones Previas

### 1. Verificar Puerto Disponible (Puerto 8085)

#### En Windows (PowerShell):
```powershell
# Verificar si el puerto 8085 está en uso
netstat -ano | findstr :8085

# Si hay un proceso usando el puerto, terminarlo (reemplazar <PID> con el número del proceso)
taskkill /PID <PID> /F

# O cambiar el puerto en application.properties a otro disponible (ej: 8086, 8087)
```

#### En Linux/Mac:
```bash
# Verificar si el puerto 8085 está en uso
lsof -i :8085
# o
netstat -tlnp | grep :8085

# Si hay un proceso, terminarlo
kill -9 <PID>

# O cambiar el puerto en application.properties
```

### 2. Verificar MySQL Esté Activo

#### En Windows:
```powershell
# Verificar servicio MySQL
Get-Service -Name MySQL*

# Si no está corriendo, iniciarlo
Start-Service -Name MySQL80
# (Ajustar el nombre según tu instalación)
```

#### En Linux:
```bash
# Verificar estado
sudo systemctl status mysql
# o
sudo systemctl status mysqld

# Si no está corriendo, iniciarlo
sudo systemctl start mysql
```

#### Verificar Conexión:
```bash
# Conectar a MySQL
mysql -u root -p

# Verificar que la base de datos existe
SHOW DATABASES LIKE 'agrosft';

# Si no existe, crearla ejecutando:
source setup_database.sql;
```

### 3. Verificar Configuración de Base de Datos

Editar `src/main/resources/application.properties` y verificar:

```properties
# Base de datos - NOMBRE CORRECTO: agrosft
spring.datasource.url=jdbc:mysql://localhost:3306/agrosft
spring.datasource.username=root
spring.datasource.password=TU_PASSWORD_AQUI  # ⚠️ IMPORTANTE: Configurar tu contraseña de MySQL

# Puerto del servidor
server.port=8085
```

**⚠️ IMPORTANTE:** 
- Si el puerto 8085 está ocupado, cambiar a otro puerto (ej: 8086, 8087, 8088)
- Si MySQL usa otro puerto (no 3306), ajustar la URL
- Configurar la contraseña de MySQL si no está vacía

### 4. Verificar Java y Maven

```bash
# Verificar Java (debe ser 17 o superior)
java -version

# Verificar Maven
mvn -version
```

## 🚀 Pasos de Ejecución

### Paso 1: Limpiar y Compilar el Proyecto

```bash
# Limpiar proyecto anterior
mvn clean

# Compilar proyecto
mvn clean compile

# Si hay errores, instalar dependencias
mvn clean install -DskipTests
```

### Paso 2: Verificar Base de Datos

```sql
-- Conectar a MySQL
mysql -u root -p

-- Verificar que la base existe
USE agrosft;

-- Verificar tablas principales
SHOW TABLES;

-- Deberías ver: usuarios, roles, cultivos, ganado, actividades, tratamientos, etc.
```

Si las tablas no existen, ejecutar:
```sql
source setup_database.sql;
```

### Paso 3: Ejecutar la Aplicación

```bash
# Opción 1: Con Maven
mvn spring-boot:run

# Opción 2: Con JAR compilado
mvn clean package
java -jar target/Agrosotf-crud-0.0.1-SNAPSHOT.jar
```

### Paso 4: Verificar que la Aplicación Esté Corriendo

1. Abrir navegador en: `http://localhost:8085`
2. Deberías ver la página de inicio o login
3. Probar login con:
   - Email: `admin@agrosoft.local`
   - Password: `admin123`

## 🛠️ Solución de Problemas Comunes

### Error: "Port 8085 is already in use"

**Solución 1:** Cambiar puerto en `application.properties`:
```properties
server.port=8086
```

**Solución 2:** Terminar proceso que usa el puerto (ver sección 1)

### Error: "Cannot connect to MySQL"

**Verificar:**
1. MySQL está corriendo
2. Puerto MySQL es 3306 (o ajustar en application.properties)
3. Usuario y contraseña son correctos
4. Base de datos `agrosft` existe

**Solución:**
```sql
-- Crear base de datos si no existe
CREATE DATABASE IF NOT EXISTS agrosft CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Ejecutar script de creación
source setup_database.sql;
```

### Error: "Table doesn't exist"

**Solución:**
```sql
-- Ejecutar script completo
source setup_database.sql;
```

### Error de Compilación

**Solución:**
```bash
# Limpiar completamente
mvn clean

# Reinstalar dependencias
mvn clean install -DskipTests

# Verificar versión de Java (debe ser 17+)
java -version
```

## ✅ Checklist Final

Antes de ejecutar, verificar:

- [ ] Puerto 8085 está disponible (o configurado otro puerto)
- [ ] MySQL está corriendo y accesible
- [ ] Base de datos `agrosft` existe
- [ ] Tablas están creadas (ejecutar setup_database.sql si es necesario)
- [ ] Credenciales de MySQL están configuradas en application.properties
- [ ] Java 17+ está instalado
- [ ] Maven está instalado
- [ ] Proyecto compila sin errores (`mvn clean compile`)

## 📝 Notas Importantes

1. **Nombre de la base de datos:** `agrosft` (no `agrosoft`)
2. **Puerto por defecto:** `8085` (cambiable en application.properties)
3. **Puerto MySQL:** `3306` (verificar si es diferente)
4. **Usuario por defecto:** `admin@agrosoft.local` / `admin123`
5. **El DataInitializer creará usuarios automáticamente si la BD está vacía**

## 🎯 Comando Rápido de Verificación

```bash
# Verificar puerto
netstat -ano | findstr :8085  # Windows
# o
lsof -i :8085  # Linux/Mac

# Verificar MySQL
mysql -u root -p -e "SHOW DATABASES LIKE 'agrosft';"

# Compilar y ejecutar
mvn clean package && java -jar target/Agrosotf-crud-0.0.1-SNAPSHOT.jar
```

