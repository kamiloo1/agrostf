# Manual Técnico - AgroSoft CRUD

**Versión actualizada a la fecha del proyecto**  
Sistema de gestión agropecuaria: usuarios, ganado, cultivos, actividades, tratamientos, notificaciones, reportes y clima.

---

## 1. Introducción

### 1.1 Propósito del sistema

AgroSoft CRUD es una aplicación web para la gestión de establecimientos agropecuarios. Permite:

- **Administración de usuarios** con roles (Administrador, Veterinario, Trabajador).
- **Gestión de ganado**: registro, actualización, estado de salud, tratamientos asociados.
- **Gestión de cultivos**: registro, áreas, estados, fechas de siembra/cosecha.
- **Actividades por cultivo**: tipo, descripción, trabajador responsable, estado.
- **Tratamientos veterinarios** vinculados al ganado.
- **Notificaciones** en el header y **auditoría** de acciones críticas.
- **Recuperación de contraseña** por correo con token de un solo uso.
- **Reportes en PDF/Excel** y **consulta de clima** (API OpenWeatherMap).
- **Envío de correos** (plantillas, recuperación de contraseña).

### 1.2 Alcance

El manual describe la arquitectura, tecnologías, modelo de datos, flujos principales y diagramas (clases, flujo y actividades) del proyecto a la fecha.

---

## 2. Stack tecnológico

| Tecnología        | Versión / Uso |
|-------------------|----------------|
| Java              | 17             |
| Spring Boot       | 3.5.11         |
| Spring Security   | (incluido)     |
| Spring Data JPA   | (incluido)     |
| Thymeleaf         | (incluido)     |
| MySQL             | 8 (XAMPP)      |
| Apache POI        | 5.2.5 (Excel)  |
| iText             | 7.2.5 (PDF)    |
| JFreeChart        | 1.5.3 (gráficos)|
| Lombok            | (opcional)     |

**Base de datos:** MySQL, nombre por defecto `agrostf`, puerto 3306 (XAMPP).  
**Servidor:** Puerto 8085 (configurable en `application.properties`).

---

## 3. Estructura del proyecto

```
agrosoft-crud final/
├── pom.xml
├── src/main/java/com/example/agrosoft1/crud/
│   ├── AgrosotfCrudApplication.java
│   ├── config/          # SecurityConfig, DataInitializer, ClimaProperties, etc.
│   ├── controller/      # Login, Registro, Dashboard, Ganado, Cultivo, etc.
│   ├── entity/          # Usuario, Role, Ganado, Cultivo, Tratamiento, Actividad, etc.
│   ├── repository/      # JpaRepository por entidad
│   ├── service/         # Lógica de negocio
│   ├── util/            # ExcelImporter, ExcelExporter, PdfExporter
│   └── pattern/         # Patrones (factory, observer, singleton, dao)
├── src/main/resources/
│   ├── application.properties
│   ├── static/          # CSS, JS, imágenes
│   └── templates/      # Vistas Thymeleaf (login, dashboard, etc.)
├── DIAGRAMA_CLASES.md
├── DIAGRAMA_FLUJO.md
└── DIAGRAMA_ACTIVIDADES.md
```

---

## 4. Modelo de datos (entidades)

### 4.1 Entidades principales

| Entidad            | Tabla               | Descripción breve |
|--------------------|---------------------|-------------------|
| Usuario            | usuarios            | Usuarios del sistema (correo, contraseña, rol). |
| Role               | roles               | Roles: ADMIN, VETERINARIO, TRABAJADOR. |
| Ganado             | ganado              | Animales (tipo, raza, edad, peso, estado de salud). |
| Cultivo            | cultivos            | Cultivos (nombre, descripción, activo). |
| Paciente           | paciente            | Pacientes (nombre, especie, raza, estado). |
| Tratamiento        | tratamientos        | Tratamientos vinculados a Ganado. |
| Actividad          | actividades         | Actividades vinculadas a Cultivo. |
| Notificacion       | notificaciones      | Notificaciones por usuario (campana header). |
| Auditoria           | auditoria           | Registro de acciones críticas. |
| TokenRecuperacion  | token_recuperacion  | Tokens para recuperación de contraseña. |
| PlantillaCorreo    | plantillas_correo   | Plantillas de correo reutilizables. |

### 4.2 Relaciones principales

- **Usuario** N:1 **Role**
- **Notificacion** N:1 **Usuario**
- **TokenRecuperacion** N:1 **Usuario**
- **Tratamiento** N:1 **Ganado**
- **Actividad** N:1 **Cultivo**

---

## 5. Capas de la aplicación

### 5.1 Resumen de capas

| Capa            | Componentes principales |
|-----------------|--------------------------|
| **Entidades**   | Usuario, Role, Ganado, Cultivo, Paciente, Tratamiento, Actividad, Notificacion, Auditoria, TokenRecuperacion, PlantillaCorreo |
| **Repositorios**| Una interfaz JpaRepository por entidad |
| **Servicios**   | UsuarioService, GanadoService, CultivoService, TratamientoService, ActividadService, NotificacionService, AuditoriaService, RecuperacionContrasenaService, EmailService, ClimaService, ReportePdfService, VeterinarioService, CustomUserDetailsService |
| **Controladores**| Login, Registro, Cuenta, Admin, Dashboard, Ganado, Cultivo, Tratamiento, Actividad, Paciente, RecuperarContrasena, Email, Reporte, Clima, Busquedas, SessionConfig, CargaDatos, DataLoad, ViewController, VetController, TrabajadorController, CustomErrorController |
| **Config**      | SecurityConfig, SecurityHeadersConfig, GlobalControllerAdvice, DataInitializer, ClimaProperties, WebClientConfig, SqlDataLoader |
| **Util**        | ExcelImporter, ExcelExporter, PdfExporter |

### 5.2 Flujo de una petición

```
Usuario (navegador) → Controller → Service → Repository → Base de datos
                         ↑_______________|_______________|
                         (vista / redirect / JSON)
```

---

## 6. Seguridad y autenticación

- **Spring Security:** login por formulario, sesión HTTP, redirección por rol tras login.
- **Roles:** ROLE_ADMIN, ROLE_VETERINARIO, ROLE_TRABAJADOR. El rol ADMIN no se puede elegir en el registro público; solo lo asigna un administrador.
- **Rutas públicas:** `/login`, `/registro`, `/recuperar`, `/recuperar/restablecer` (con token), recursos estáticos.
- **Rutas por rol:** `/admin/*` (administrador), `/dashboard/veterinario`, `/trabajador/*` (trabajador), etc.
- **Sesión:** timeout configurable (por defecto 30 minutos), cookie httpOnly.
- **Recuperación de contraseña:** token de un solo uso con expiración (24 h), enlace por correo.

---

## 7. Configuración relevante (application.properties)

- **Base de datos:** `spring.datasource.url=jdbc:mysql://localhost:3306/agrostf?...`, usuario/contraseña según XAMPP.
- **JPA:** `ddl-auto=update`, dialecto MySQL8, UTF-8.
- **Servidor:** `server.port=8085`, páginas de error personalizadas.
- **Sesión:** `server.servlet.session.timeout=30m`, `app.session.timeout.minutes=30`.
- **Correo:** SMTP Gmail (host, port, username, password de aplicación). Configurar con cuenta real.
- **Clima:** `app.clima.api.key`, `app.clima.api.url` (OpenWeatherMap); clave real según documentación del proyecto.
- **Logging:** nivel DEBUG para `com.example.agrosoft1`, archivo `logs/agrosoft.log`.

---

## 8. Diagramas del sistema

El proyecto incluye tres documentos con diagramas en sintaxis Mermaid (visualizables en GitHub, VS Code/Cursor con extensión Mermaid, o en [mermaid.live](https://mermaid.live)):

### 8.1 Diagrama de clases (DIAGRAMA_CLASES.md)

- **Entidades:** atributos y relaciones (Usuario–Role, Notificacion–Usuario, TokenRecuperacion–Usuario, Tratamiento–Ganado, Actividad–Cultivo).
- **Repositorios:** interfaces que extienden JpaRepository por entidad.
- **Servicios:** dependencias entre servicios y repositorios.
- **Controladores:** dependencias con servicios/repositorios.
- **Configuración y utilidades:** SecurityConfig, DataInitializer, ClimaProperties, ExcelImporter, ExcelExporter.

### 8.2 Diagramas de flujo (DIAGRAMA_FLUJO.md)

- Flujo de inicio de sesión (validación Spring Security, redirección por rol).
- Flujo de registro (validaciones, guardado, autologin y redirección).
- Flujo de recuperación de contraseña (solicitud, enlace, restablecer).
- Flujo CRUD de Ganado (listar, crear, actualizar, eliminar/desactivar).
- Flujo de acceso al dashboard por rol.
- Flujo general de una petición (capas).
- Flujo de notificaciones al crear/actualizar entidades.

### 8.3 Diagrama de actividades general (DIAGRAMA_ACTIVIDADES.md)

- Un único diagrama de actividades que resume:
  - Entrada al sistema (login, registro, recuperar contraseña).
  - Validaciones y redirección al dashboard según rol.
  - Actividades por rol (administrador: usuarios, ganado, cultivos, reportes, clima, notificaciones; veterinario: tratamientos, ganado, reportes; trabajador: actividades, cultivos, notificaciones).
  - Auditoría y notificaciones asociadas a CRUD.
  - Decisión de seguir en el sistema o cerrar sesión.

---

## 9. Funcionalidades por rol

### 9.1 Administrador

- Dashboard con resumen (usuarios, cultivos, ganado, actividades).
- CRUD de usuarios y asignación de roles.
- CRUD de ganado y exportación PDF/estadísticas.
- CRUD de cultivos, exportación PDF/Excel, cambio de estado.
- Acceso a reportes, clima y notificaciones.
- Carga de datos (Excel) y configuración de sesión/notificaciones según implementación.

### 9.2 Veterinario

- Dashboard con tratamientos, reportes y revisiones.
- Gestión de tratamientos vinculados al ganado.
- Consulta de ganado y reportes.
- Notificaciones.

### 9.3 Trabajador

- Dashboard con actividades y cultivos.
- CRUD de actividades por cultivo (`/trabajador/actividades`).
- Consulta de cultivos.
- Notificaciones.

---

## 10. Referencias a documentos del proyecto

- **DIAGRAMA_CLASES.md** – Diagramas de clases (entidades, repositorios, servicios, controladores, config).
- **DIAGRAMA_FLUJO.md** – Diagramas de flujo (login, registro, recuperación, CRUD, dashboard, notificaciones).
- **DIAGRAMA_ACTIVIDADES.md** – Diagrama de actividades general del sistema.
- **application.properties** – Configuración de BD, servidor, sesión, correo, clima y logging.
- **CONFIGURAR_API_CLIMA_REAL.md** – (Si existe) instrucciones para la API de clima.

---

## 11. Requisitos de ejecución

- JDK 17.
- Maven (para compilar y ejecutar).
- MySQL en ejecución (por ejemplo XAMPP con MySQL en el puerto 3306).
- Base de datos `agrostf` creada o permitir `createDatabaseIfNotExist=true` en la URL.
- Para correo: cuenta Gmail con contraseña de aplicación configurada en `application.properties`.
- Para clima real: API key de OpenWeatherMap en `app.clima.api.key`.

**Comando típico:** `mvn spring-boot:run` desde la raíz del proyecto (o ejecutar la clase `AgrosotfCrudApplication`).

---

*Manual técnico actualizado con la información del proyecto AgroSoft CRUD a la fecha. Los diagramas detallados se encuentran en los archivos DIAGRAMA_CLASES.md, DIAGRAMA_FLUJO.md y DIAGRAMA_ACTIVIDADES.md.*
