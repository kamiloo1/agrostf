# 🚀 INSTRUCCIONES COMPLETAS DE INSTALACIÓN - AgroSoft

## 📋 REQUISITOS PREVIOS

### 1. Software Necesario
- **Java 17** o superior
- **Maven 3.6+**
- **MySQL 8.0** o superior
- **Git** (opcional)

### 2. Verificar Instalaciones
```bash
# Verificar Java
java -version

# Verificar Maven
mvn -version

# Verificar MySQL
mysql --version
```

## 🗄️ CONFIGURACIÓN DE BASE DE DATOS

### Paso 1: Crear Base de Datos
```sql
-- Conectar a MySQL como root
mysql -u root -p

-- Ejecutar el script de configuración
source setup_database.sql;
```

### Paso 2: Verificar Instalación
```sql
-- Verificar que las tablas se crearon
SHOW TABLES;

-- Verificar datos insertados
SELECT COUNT(*) as usuarios FROM usuarios;
SELECT COUNT(*) as cultivos FROM cultivos;
SELECT COUNT(*) as ganado FROM ganado;
```

## 🔧 CONFIGURACIÓN DEL PROYECTO

### Paso 1: Verificar Configuración
Editar `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/agrosft
spring.datasource.username=root
spring.datasource.password=TU_PASSWORD_AQUI
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.sql.init.mode=never
server.port=8085
```

### Paso 2: Compilar Proyecto
```bash
# Limpiar y compilar
mvn clean compile

# Si hay errores, instalar dependencias
mvn clean install
```

## 🚀 EJECUCIÓN DEL PROYECTO

### Opción 1: Con Maven
```bash
mvn spring-boot:run
```

### Opción 2: Con JAR
```bash
# Compilar JAR
mvn clean package

# Ejecutar JAR
java -jar target/Agrosotf-crud-0.0.1-SNAPSHOT.jar
```

### Opción 3: En IDE
- Importar proyecto en IntelliJ IDEA o Eclipse
- Ejecutar `AgrosotfCrudApplication.java`

## 🌐 ACCESO A LA APLICACIÓN

### URL Principal
```
http://localhost:8085
```

### Usuarios de Prueba
| Email | Contraseña | Rol |
|-------|------------|-----|
| admin@agrosoft.com | 123456 | Administrador |
| veterinario@agrosoft.com | 123456 | Veterinario |
| trabajador@agrosoft.com | 123456 | Trabajador |
| admin2@agrosoft.com | 123456 | Administrador |
| vet2@agrosoft.com | 123456 | Veterinario |
| trabajador2@agrosoft.com | 123456 | Trabajador |

## 📁 ESTRUCTURA DEL PROYECTO COMPLETO

```
agrosoft-crud1/
├── src/main/java/com/example/agrosoft1/crud/
│   ├── AgrosotfCrudApplication.java
│   ├── config/
│   │   └── SecurityConfig.java
│   ├── controller/
│   │   ├── AdminController.java
│   │   ├── CultivoController.java
│   │   ├── DashboardController.java
│   │   ├── LoginController.java
│   │   ├── RegistroController.java
│   │   ├── TrabajadorController.java
│   │   ├── VetController.java
│   │   └── ViewController.java
│   ├── entity/
│   │   ├── Cultivo.java
│   │   └── Usuario.java
│   ├── repository/
│   │   ├── CultivoRepository.java
│   │   └── UsuarioRepository.java
│   └── service/
│       ├── CultivoService.java
│       ├── CustomUserDetailsService.java
│       ├── UsuarioService.java
│       └── VeterinarioService.java
├── src/main/resources/
│   ├── static/images/
│   ├── templates/
│   │   ├── dashboard/
│   │   │   ├── administrador.html
│   │   │   ├── cultivos.html
│   │   │   ├── trabajador.html
│   │   │   ├── usuarios.html
│   │   │   └── veterinario.html
│   │   ├── inicio.html
│   │   ├── login.html
│   │   └── registrarse.html
│   ├── application.properties
│   └── data.sql
├── pom.xml
├── setup_database.sql
├── README.md
└── INSTRUCCIONES_INSTALACION.md
```

## 🔍 FUNCIONALIDADES IMPLEMENTADAS

### ✅ Sistema de Autenticación
- Login con Spring Security
- Registro de nuevos usuarios
- Roles: Administrador, Veterinario, Trabajador
- Encriptación de contraseñas

### ✅ Gestión de Usuarios
- CRUD completo de usuarios
- Asignación de roles
- Validaciones de seguridad

### ✅ Gestión de Cultivos
- CRUD completo de cultivos
- Categorización por tipo
- Control de áreas

### ✅ Dashboard por Roles
- **Administrador**: Vista general, gestión de usuarios
- **Veterinario**: Estadísticas veterinarias, tratamientos
- **Trabajador**: Vista de cultivos, actividades

### ✅ Base de Datos Completa
- Tablas: usuarios, cultivos, ganado, tratamientos, actividades
- Datos de prueba pre-cargados
- Vistas y procedimientos almacenados

## 🛠️ SOLUCIÓN DE PROBLEMAS

### Error de Conexión a MySQL
```bash
# Verificar que MySQL esté ejecutándose
sudo systemctl status mysql

# Iniciar MySQL si está detenido
sudo systemctl start mysql

# Verificar puerto
netstat -tlnp | grep :3306
```

### Error de Puerto Ocupado
```bash
# Cambiar puerto en application.properties
server.port=8082

# O matar proceso en puerto 8085
netstat -ano | findstr :8085
taskkill /PID <PID_NUMBER> /F
```

### Error de Compilación
```bash
# Limpiar proyecto
mvn clean

# Reinstalar dependencias
mvn clean install

# Verificar versión de Java
java -version
```

### Error de Base de Datos
```sql
-- Verificar que la base existe
SHOW DATABASES;

-- Verificar usuario y permisos
SELECT User, Host FROM mysql.user WHERE User = 'root';

-- Recrear base de datos si es necesario
DROP DATABASE IF EXISTS agrosft;
CREATE DATABASE agrosft CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- Luego ejecutar el script completo
source setup_database.sql;
```

## 📦 COMPRIMIR PROYECTO

### En Windows (PowerShell)
```powershell
# Comprimir todo el proyecto
Compress-Archive -Path "agrosoft-crud1" -DestinationPath "agrosoft-proyecto-completo.zip"
```

### En Linux/Mac
```bash
# Comprimir todo el proyecto
tar -czf agrosoft-proyecto-completo.tar.gz agrosoft-crud1/
```

## 🎯 PRÓXIMOS PASOS

1. **Ejecutar el script de base de datos**
2. **Configurar application.properties**
3. **Compilar y ejecutar el proyecto**
4. **Acceder a http://localhost:8085**
5. **Probar con usuarios de prueba**

## 📞 SOPORTE

Si tienes problemas:
1. Verificar que todos los requisitos estén instalados
2. Revisar los logs de la aplicación
3. Verificar la conexión a MySQL
4. Comprobar que no hay conflictos de puertos

¡El proyecto está 100% funcional y listo para usar! 🚀
