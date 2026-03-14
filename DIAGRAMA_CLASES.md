# Diagrama de clases - AgroSoft CRUD

Diagrama de clases del proyecto AgroSoft (Spring Boot): entidades, repositorios, servicios y controladores principales.

---

## 1. Entidades (modelo de dominio)

```mermaid
classDiagram
    class Usuario {
        -Integer id
        -String nombre
        -String correo
        -String telefono
        -String numeroDocumento
        -String password
        -Boolean activo
        -LocalDateTime fechaCreacion
        -LocalDateTime ultimoLogin
        +getId()
        +getCorreo()
        +getRole()
    }

    class Role {
        -Integer id
        -String nombre
        -String descripcion
        -LocalDateTime creadoEn
        +getId()
        +getNombre()
    }

    class Ganado {
        -Long idGanado
        -String tipo
        -String raza
        -Integer edad
        -Double peso
        -String estadoSalud
        -LocalDate fechaNacimiento
        -LocalDateTime fechaCreacion
        -Boolean activo
    }

    class Cultivo {
        -Long id
        -String nombre
        -String descripcion
        -LocalDateTime fechaCreacion
        -Boolean activo
    }

    class Paciente {
        -Long id
        -String nombre
        -String especie
        -String raza
        -String edad
        -String estado
        -String observaciones
    }

    class Tratamiento {
        -Long idTratamiento
        -String tipoTratamiento
        -LocalDate fechaTratamiento
        -String observaciones
        -String veterinarioResponsable
        -BigDecimal costo
        -LocalDateTime fechaCreacion
    }

    class Actividad {
        -Long idActividad
        -String tipoActividad
        -String descripcion
        -LocalDate fechaActividad
        -String trabajadorResponsable
        -String estado
        -LocalDateTime fechaCreacion
    }

    class Notificacion {
        -Long id
        -String mensaje
        -String tipo
        -Boolean leida
        -LocalDateTime fechaCreacion
        -String enlace
    }

    class Auditoria {
        -Long id
        -String usuario
        -String accion
        -String entidad
        -String idEntidad
        -LocalDateTime fecha
        -String detalles
    }

    class TokenRecuperacion {
        -Integer id
        -String token
        -LocalDateTime fechaExpiracion
        +isExpirado()
    }

    class PlantillaCorreo {
        -Integer id
        -String nombre
        -String asunto
        -String mensaje
        -String categoria
        -Boolean activo
        -LocalDateTime fechaCreacion
        -LocalDateTime fechaActualizacion
    }

    Usuario "N" --> "1" Role : role_id
    Notificacion "N" --> "1" Usuario : usuario_id
    TokenRecuperacion "N" --> "1" Usuario : usuario_id
    Tratamiento "N" --> "1" Ganado : id_ganado
    Actividad "N" --> "1" Cultivo : id_cultivo
```

---

## 2. Repositorios (capa de datos)

```mermaid
classDiagram
    class JpaRepository~T,ID~ {
        <<interface>>
        +save()
        +findById()
        +findAll()
        +delete()
    }

    class UsuarioRepository {
        <<interface>>
    }
    class RoleRepository { <<interface>> }
    class GanadoRepository { <<interface>> }
    class CultivoRepository { <<interface>> }
    class PacienteRepository { <<interface>> }
    class TratamientoRepository { <<interface>> }
    class ActividadRepository { <<interface>> }
    class NotificacionRepository { <<interface>> }
    class AuditoriaRepository { <<interface>> }
    class TokenRecuperacionRepository { <<interface>> }
    class PlantillaCorreoRepository { <<interface>> }

    JpaRepository <|-- UsuarioRepository
    JpaRepository <|-- RoleRepository
    JpaRepository <|-- GanadoRepository
    JpaRepository <|-- CultivoRepository
    JpaRepository <|-- PacienteRepository
    JpaRepository <|-- TratamientoRepository
    JpaRepository <|-- ActividadRepository
    JpaRepository <|-- NotificacionRepository
    JpaRepository <|-- AuditoriaRepository
    JpaRepository <|-- TokenRecuperacionRepository
    JpaRepository <|-- PlantillaCorreoRepository

    UsuarioRepository ..> Usuario : persiste
    RoleRepository ..> Role : persiste
    GanadoRepository ..> Ganado : persiste
    CultivoRepository ..> Cultivo : persiste
    PacienteRepository ..> Paciente : persiste
    TratamientoRepository ..> Tratamiento : persiste
    ActividadRepository ..> Actividad : persiste
    NotificacionRepository ..> Notificacion : persiste
    AuditoriaRepository ..> Auditoria : persiste
    TokenRecuperacionRepository ..> TokenRecuperacion : persiste
    PlantillaCorreoRepository ..> PlantillaCorreo : persiste
```

---

## 3. Servicios y dependencias

```mermaid
classDiagram
    class UsuarioService {
        -UsuarioRepository usuarioRepository
        -RoleRepository roleRepository
        -AuditoriaService auditoriaService
        +findByCorreo()
        +save()
        +findAll()
    }
    class GanadoService {
        -GanadoRepository ganadoRepository
        -TratamientoService tratamientoService
        -AuditoriaService auditoriaService
        -NotificacionService notificacionService
    }
    class CultivoService {
        -CultivoRepository cultivoRepository
        -AuditoriaService auditoriaService
        -NotificacionService notificacionService
    }
    class TratamientoService {
        -TratamientoRepository tratamientoRepository
        -GanadoService ganadoService
        -NotificacionService notificacionService
    }
    class ActividadService {
        -ActividadRepository actividadRepository
        -CultivoService cultivoService
    }
    class NotificacionService {
        -NotificacionRepository notificacionRepository
        -UsuarioService usuarioService
    }
    class AuditoriaService {
        -AuditoriaRepository auditoriaRepository
    }
    class RecuperacionContrasenaService {
        -UsuarioRepository usuarioRepository
        -TokenRecuperacionRepository tokenRepository
        -EmailService emailService
    }
    class EmailService { }
    class ClimaService { }
    class ReportePdfService {
        -GanadoRepository ganadoRepository
        -TratamientoRepository tratamientoRepository
    }
    class VeterinarioService {
        -TratamientoRepository tratamientoRepository
    }
    class CustomUserDetailsService {
        -UsuarioService usuarioService
    }

    UsuarioService --> UsuarioRepository : usa
    UsuarioService --> RoleRepository : usa
    UsuarioService --> AuditoriaService : usa
    GanadoService --> GanadoRepository : usa
    GanadoService --> TratamientoService : usa
    GanadoService --> AuditoriaService : usa
    GanadoService --> NotificacionService : usa
    CultivoService --> CultivoRepository : usa
    CultivoService --> AuditoriaService : usa
    CultivoService --> NotificacionService : usa
    TratamientoService --> TratamientoRepository : usa
    TratamientoService --> GanadoService : usa
    TratamientoService --> NotificacionService : usa
    ActividadService --> ActividadRepository : usa
    ActividadService --> CultivoService : usa
    NotificacionService --> NotificacionRepository : usa
    NotificacionService --> UsuarioService : usa
    RecuperacionContrasenaService --> UsuarioRepository : usa
    RecuperacionContrasenaService --> TokenRecuperacionRepository : usa
    RecuperacionContrasenaService --> EmailService : usa
    CustomUserDetailsService --> UsuarioService : usa
```

---

## 4. Controladores y servicios

```mermaid
classDiagram
    class LoginController { }
    class RegistroController {
        -UsuarioService usuarioService
        -RoleRepository roleRepository
        -UserDetailsService userDetailsService
    }
    class CuentaController {
        -UsuarioService usuarioService
    }
    class AdminController {
        -UsuarioService usuarioService
        -RoleRepository roleRepository
    }
    class DashboardController {
        -UsuarioService usuarioService
        -CultivoService cultivoService
        -VeterinarioService veterinarioService
        -GanadoService ganadoService
        -ActividadService actividadService
    }
    class GanadoController {
        -GanadoService ganadoService
    }
    class CultivoController {
        -CultivoService cultivoService
    }
    class TratamientoController {
        -TratamientoService tratamientoService
        -GanadoService ganadoService
    }
    class ActividadController {
        -ActividadService actividadService
        -CultivoService cultivoService
    }
    class PacienteController {
        -PacienteRepository pacienteRepository
    }
    class RecuperarContrasenaController {
        -RecuperacionContrasenaService recuperacionService
    }
    class EmailController {
        -EmailService emailService
        -PlantillaCorreoRepository plantillaCorreoRepository
        -UsuarioRepository usuarioRepository
    }
    class ReporteController {
        -ReportePdfService reportePdfService
    }
    class ClimaController {
        -ClimaService climaService
    }
    class BusquedasController {
        -UsuarioRepository usuarioRepository
        -GanadoRepository ganadoRepository
        -CultivoRepository cultivoRepository
        -TratamientoRepository tratamientoRepository
        -ActividadRepository actividadRepository
    }
    class SessionConfigController {
        -NotificacionService notificacionService
    }

    LoginController ..> Login
    RegistroController --> UsuarioService
    CuentaController --> UsuarioService
    AdminController --> UsuarioService
    DashboardController --> UsuarioService
    DashboardController --> CultivoService
    DashboardController --> GanadoService
    DashboardController --> ActividadService
    GanadoController --> GanadoService
    CultivoController --> CultivoService
    TratamientoController --> TratamientoService
    ActividadController --> ActividadService
    RecuperarContrasenaController --> RecuperacionContrasenaService
    ReporteController --> ReportePdfService
    ClimaController --> ClimaService
    SessionConfigController --> NotificacionService
```

---

## 5. Configuración y utilidades

```mermaid
classDiagram
    class SecurityConfig {
        Configuración Spring Security
    }
    class SecurityHeadersConfig { }
    class GlobalControllerAdvice {
        -UsuarioService usuarioService
    }
    class DataInitializer {
        -UsuarioRepository usuarioRepository
        -RoleRepository roleRepository
        -GanadoRepository ganadoRepository
        -TratamientoRepository tratamientoRepository
        -PlantillaCorreoRepository plantillaCorreoRepository
        -NotificacionService notificacionService
        -NotificacionRepository notificacionRepository
    }
    class ClimaProperties {
        -String apiKey
        -String apiUrl
        -int timeoutSeconds
        -boolean cacheEnabled
    }
    class WebClientConfig { }
    class SqlDataLoader { }

    class ExcelImporter {
        -UsuarioRepository usuarioRepository
        -RoleRepository roleRepository
        -GanadoRepository ganadoRepository
        -TratamientoRepository tratamientoRepository
        -CultivoRepository cultivoRepository
    }
    class ExcelExporter {
        +exportarUsuarios()
        +exportarCultivos()
    }

    ClimaService ..> ClimaProperties : usa
    DataInitializer ..> NotificacionService : usa
    GlobalControllerAdvice ..> UsuarioService : usa
```

---

## Resumen de capas

| Capa           | Componentes principales |
|----------------|-------------------------|
| **Entidades**  | Usuario, Role, Ganado, Cultivo, Paciente, Tratamiento, Actividad, Notificacion, Auditoria, TokenRecuperacion, PlantillaCorreo |
| **Repositorios** | Uno por entidad (JpaRepository) |
| **Servicios**  | UsuarioService, GanadoService, CultivoService, TratamientoService, ActividadService, NotificacionService, AuditoriaService, RecuperacionContrasenaService, EmailService, ClimaService, ReportePdfService, VeterinarioService, CustomUserDetailsService |
| **Controladores** | Login, Registro, Cuenta, Admin, Dashboard, Ganado, Cultivo, Tratamiento, Actividad, Paciente, RecuperarContrasena, Email, Reporte, Clima, Busquedas, SessionConfig, CargaDatos, DataLoad, ViewController, VetController, TrabajadorController, CustomErrorController |
| **Config**     | SecurityConfig, SecurityHeadersConfig, GlobalControllerAdvice, DataInitializer, ClimaProperties, WebClientConfig, SqlDataLoader |
| **Util**       | ExcelImporter, ExcelExporter |

---

*Generado para el proyecto AgroSoft CRUD (Spring Boot).*
