# Diagrama de actividades general - AgroSoft CRUD

Un único diagrama de actividades que resume el flujo general del proyecto: acceso, autenticación, dashboards por rol y actividades principales del sistema.

---

## Diagrama de actividades general del proyecto

```mermaid
flowchart TD
    INICIO([Usuario accede al sistema])
    INICIO --> TIPO{¿Qué desea hacer?}
    TIPO -->|Iniciar sesión| LOGIN[Mostrar formulario de login]
    TIPO -->|Registrarse| REG[Mostrar formulario de registro]
    TIPO -->|Recuperar contraseña| RECUP[Mostrar formulario: ingresar correo]

    LOGIN --> INGRESA[Usuario ingresa correo y contraseña]
    INGRESA --> VALIDA{Spring Security valida}
    VALIDA -->|Credenciales incorrectas / inactivo / sin rol| ERROR_LOG[Mostrar error y volver a login]
    ERROR_LOG --> LOGIN
    VALIDA -->|Correcto| DASH_ENTRY[Acceder a /dashboard]

    REG --> COMPLETA[Completa nombre, email, documento, contraseña, rol]
    COMPLETA --> VAL_CORREO{¿Correo ya existe?}
    VAL_CORREO -->|Sí| ERR_REG[Mostrar error]
    ERR_REG --> REG
    VAL_CORREO -->|No| VAL_DOC{¿Documento ya existe?}
    VAL_DOC -->|Sí| ERR_REG
    VAL_DOC -->|No| GUARDA_USR[UsuarioService: guardar usuario]
    GUARDA_USR --> AUTO_LOG[Autenticar y redirigir por rol]
    AUTO_LOG --> DASH_ENTRY

    RECUP --> ENVIA_CORREO[RecuperacionContrasenaService: si correo existe, enviar enlace]
    ENVIA_CORREO --> MSG[Mostrar mensaje genérico]
    MSG --> RECUP
    RECUP -->|Usuario hace clic en enlace| RESTABLECE[Formulario restablecer contraseña]
    RESTABLECE --> VAL_TOKEN{¿Token válido?}
    VAL_TOKEN -->|No| RECUP
    VAL_TOKEN -->|Sí| NUEVA_PWD[Ingresar nueva contraseña y confirmar]
    NUEVA_PWD --> ACT_PWD[Actualizar contraseña, eliminar token]
    ACT_PWD --> LOGIN

    DASH_ENTRY --> ROL{DashboardController: según rol}
    ROL -->|ROLE_ADMIN| PANEL_ADM[Panel administrador]
    ROL -->|ROLE_VETERINARIO| PANEL_VET[Panel veterinario]
    ROL -->|ROLE_TRABAJADOR| PANEL_TRAB[Panel trabajador]
    ROL -->|Sin rol válido| LOGIN

    PANEL_ADM --> ACT_ADM{Actividad en el sistema}
    PANEL_VET --> ACT_VET{Actividad en el sistema}
    PANEL_TRAB --> ACT_TRAB{Actividad en el sistema}

    ACT_ADM -->|Gestionar usuarios| CRUD_USR[AdminController: listar / crear / editar usuarios]
    ACT_ADM -->|Gestionar ganado| CRUD_GAN[GanadoController: listar / guardar / actualizar / eliminar]
    ACT_ADM -->|Gestionar cultivos| CRUD_CUL[CultivoController: listar / guardar / actualizar / eliminar]
    ACT_ADM -->|Ver reportes / exportar| REP_PDF[ReportePdfService, ExcelExporter, PdfExporter]
    ACT_ADM -->|Ver clima| CLIMA[ClimaController / ClimaService]
    ACT_ADM -->|Notificaciones| NOTIF[NotificacionService: ver / marcar leídas]
    CRUD_GAN --> AUDIT[AuditoriaService y NotificacionService opcionales]
    CRUD_CUL --> AUDIT
    CRUD_USR --> AUDIT

    ACT_VET -->|Tratamientos| CRUD_TRAT[TratamientoController + TratamientoService]
    ACT_VET -->|Ganado| CRUD_GAN_VET[GanadoService: consultar / reportes]
    ACT_VET -->|Reportes| REP_PDF
    ACT_VET -->|Notificaciones| NOTIF
    CRUD_TRAT --> AUDIT

    ACT_TRAB -->|Actividades por cultivo| CRUD_ACT[ActividadController: listar / guardar / actualizar / eliminar]
    ACT_TRAB -->|Cultivos| CRUD_CUL_TRAB[CultivoService: consultar]
    ACT_TRAB -->|Notificaciones| NOTIF
    CRUD_ACT --> AUDIT

    ACT_ADM -->|Elegir otra acción o cerrar sesión| FIN_USE{¿Seguir en el sistema?}
    ACT_VET -->|Elegir otra acción o cerrar sesión| FIN_USE
    ACT_TRAB -->|Elegir otra acción o cerrar sesión| FIN_USE
    FIN_USE -->|Sí| DASH_ENTRY
    FIN_USE -->|No| FIN([Fin de sesión])
```

---

## Leyenda rápida

| Elemento | Significado |
|----------|-------------|
| **Óvalos** `([ ])` | Inicio y fin del flujo |
| **Rectángulos** `[ ]` | Actividades (acciones del usuario o del sistema) |
| **Rombo** `{ }` | Decisión o bifurcación |
| **Flechas** `-->` | Orden del flujo; `-->\|texto\|` indica la condición del camino |

El diagrama cubre en un solo flujo: **acceso al sistema** (login, registro, recuperar contraseña), **entrada al dashboard según rol** (administrador, veterinario, trabajador) y las **actividades principales** de cada rol (CRUD de usuarios, ganado, cultivos, actividades, tratamientos, reportes, clima, notificaciones y auditoría), hasta **seguir usando el sistema o cerrar sesión**.

Puedes visualizarlo en editores con soporte Mermaid o en [mermaid.live](https://mermaid.live).
