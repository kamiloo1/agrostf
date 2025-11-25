# 🚀 Inicio Rápido - AgroSoft

Guía rápida para ejecutar el proyecto por primera vez.

## ⚡ Pasos Rápidos

### 1. Verificar Configuración (Opcional pero Recomendado)

**Windows:**
```powershell
.\verificar_configuracion.ps1
# o
.\verificar_configuracion.bat
```

**Linux/Mac:**
```bash
# Verificar puerto
lsof -i :8085

# Verificar MySQL
sudo systemctl status mysql
```

### 2. Configurar Base de Datos

```bash
# Conectar a MySQL
mysql -u root -p

# Ejecutar script de creación
source setup_database.sql;
# o desde línea de comandos:
mysql -u root -p < setup_database.sql
```

### 3. Configurar Credenciales (Si es necesario)

Editar `src/main/resources/application.properties`:
```properties
# Si tu MySQL tiene contraseña, configurarla aquí:
spring.datasource.password=tu_password_aqui

# Si el puerto 8085 está ocupado, cambiar a otro:
server.port=8086
```

### 4. Compilar y Ejecutar

```bash
# Compilar
mvn clean package

# Ejecutar
java -jar target/Agrosotf-crud-0.0.1-SNAPSHOT.jar
# o
mvn spring-boot:run
```

### 5. Acceder a la Aplicación

Abrir navegador en: **http://localhost:8085**

**Credenciales por defecto:**
- Email: `admin@agrosoft.local`
- Password: `admin123`

## 🔧 Solución Rápida de Problemas

### Puerto Ocupado
```properties
# Cambiar en application.properties:
server.port=8086
```

### MySQL No Conecta
1. Verificar que MySQL esté corriendo
2. Verificar credenciales en `application.properties`
3. Verificar que la base `agrosft` existe

### Error de Compilación
```bash
mvn clean install -DskipTests
```

## 📋 Checklist Pre-Ejecución

- [ ] Puerto 8085 disponible (o configurado otro)
- [ ] MySQL corriendo
- [ ] Base de datos `agrosft` creada
- [ ] Credenciales configuradas en `application.properties`
- [ ] Proyecto compilado (`mvn clean package`)

## 📚 Documentación Completa

- `VERIFICACION_INICIAL.md` - Verificación detallada
- `INSTRUCCIONES_INSTALACION.md` - Instrucciones completas
- `README.md` - Documentación general

