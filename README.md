# AgroSoft - Sistema de Gestión Agropecuaria

Sistema web desarrollado en Spring Boot para la gestión integral de una finca agropecuaria.

## Características

- **Gestión de Usuarios**: Administración de usuarios con diferentes roles (Administrador, Veterinario, Trabajador)
- **Gestión de Cultivos**: CRUD completo para el manejo de cultivos
- **Dashboard por Roles**: Interfaces específicas según el tipo de usuario
- **Autenticación Segura**: Sistema de login con Spring Security
- **Base de Datos MySQL**: Persistencia de datos con JPA/Hibernate

## Tecnologías Utilizadas

- **Backend**: Spring Boot 3.2.5, Spring Security, Spring Data JPA
- **Base de Datos**: MySQL 8.0
- **Frontend**: Thymeleaf, HTML5, CSS3, JavaScript
- **Build Tool**: Maven
- **Java Version**: 17

## Requisitos Previos

1. **Java 17** o superior
2. **Maven 3.6+**
3. **MySQL 8.0** o superior
4. **Git** (opcional)

## Configuración de la Base de Datos

1. Instalar MySQL si no está instalado
2. Crear la base de datos:
   ```sql
   CREATE DATABASE agrosft;
   ```
   O ejecutar el script completo:
   ```sql
   source setup_database.sql;
   ```
3. Actualizar las credenciales en `src/main/resources/application.properties` si es necesario:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/agrosft
   spring.datasource.username=root
   spring.datasource.password=tu_password
   ```

## Instalación y Ejecución

1. **Clonar el repositorio** (si se usa Git):
   ```bash
   git clone <url-del-repositorio>
   cd agrosoft-crud1
   ```

2. **Compilar el proyecto**:
   ```bash
   mvn clean compile
   ```

3. **Ejecutar la aplicación**:
   ```bash
   mvn spring-boot:run
   ```

4. **Acceder a la aplicación**:
   - URL: http://localhost:8085
   - El sistema se ejecutará en el puerto 8085

## Usuarios de Prueba

El sistema viene con usuarios predefinidos para pruebas:

| Email | Contraseña | Rol |
|-------|------------|-----|
| admin@agrosoft.com | 123456 | Administrador |
| veterinario@agrosoft.com | 123456 | Veterinario |
| trabajador@agrosoft.com | 123456 | Trabajador |

## Estructura del Proyecto

```
src/main/java/com/example/agrosoft1/crud/
├── config/          # Configuraciones (Security)
├── controller/      # Controladores REST
├── entity/          # Entidades JPA
├── repository/      # Repositorios de datos
├── service/         # Lógica de negocio
└── AgrosotfCrudApplication.java

src/main/resources/
├── static/          # Archivos estáticos (CSS, JS, imágenes)
├── templates/       # Plantillas Thymeleaf
├── application.properties
└── data.sql         # Datos iniciales
```

## Funcionalidades por Rol

### Administrador
- Gestión completa de usuarios
- Vista general del sistema
- Acceso a todas las funcionalidades

### Veterinario
- Dashboard con estadísticas veterinarias
- Gestión de tratamientos y reportes
- Acceso a información de ganado

### Trabajador
- Vista de cultivos asignados
- Información de trabajo
- Acceso limitado según permisos

## API Endpoints Principales

- `GET /` - Página principal
- `GET /login` - Formulario de login
- `POST /login/auth` - Procesamiento de login
- `GET /dashboard/administrador` - Panel administrador
- `GET /dashboard/veterinario` - Panel veterinario
- `GET /dashboard/trabajador` - Panel trabajador
- `GET /admin/usuarios` - Gestión de usuarios
- `GET /admin/cultivos` - Gestión de cultivos

## Desarrollo

Para desarrollo, se recomienda:

1. Usar un IDE como IntelliJ IDEA o Eclipse
2. Configurar el perfil de desarrollo en `application.properties`
3. Usar `spring.jpa.hibernate.ddl-auto=update` para desarrollo
4. Activar logs SQL con `spring.jpa.show-sql=true`

## Solución de Problemas

### Error de conexión a MySQL
- Verificar que MySQL esté ejecutándose
- Comprobar credenciales en `application.properties`
- Asegurar que la base de datos `agrosft` existe
- Ejecutar el script `setup_database.sql` para crear la base de datos y tablas

### Error de puerto ocupado
- Cambiar el puerto en `application.properties`: `server.port=8086`
- O terminar procesos que usen el puerto 8085

### Problemas de permisos
- Verificar que el usuario de MySQL tenga permisos suficientes
- Revisar la configuración de Spring Security

## Contribución

1. Fork del proyecto
2. Crear una rama para la feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit de los cambios (`git commit -am 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Crear un Pull Request

## Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## Contacto

Para soporte o consultas, contactar al equipo de desarrollo.
