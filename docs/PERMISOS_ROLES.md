# Permisos por rol - AgroSoft

## Resumen de accesos

| Recurso | Administrador | Veterinario | Trabajador |
|---------|---------------|-------------|------------|
| **Dashboard** | `/dashboard/administrador` | `/dashboard/veterinario` | `/dashboard/trabajador` |
| **Usuarios** | ✅ `/admin/usuarios` | ❌ | ❌ |
| **Cultivos** | ✅ `/admin/cultivos` | ❌ | ✅ `/admin/cultivos` |
| **Ganado** | ✅ `/admin/ganado` | ✅ `/admin/ganado` | ✅ `/admin/ganado` |
| **Tratamientos** | ✅ `/vet/tratamientos` (ver y verificar) | ✅ `/vet/tratamientos` (gestionar) | ❌ |
| **Búsquedas** | ✅ `/admin/busquedas` | ✅ `/admin/busquedas` | ✅ `/admin/busquedas` |
| **Reportes** | ✅ `/admin/reportes` | ✅ `/admin/reportes` | ✅ `/admin/reportes` |
| **Actividades** | ❌ | ✅ `/trabajador/actividades` (asignar/marcar) | ✅ `/trabajador/actividades` (ver/efectuar) |
| **Correos** | ✅ `/admin/correos` | ❌ | ❌ |
| **Clima** | ✅ `/clima` | ✅ `/clima` | ✅ `/clima` |
| **Mi cuenta** | ✅ `/cuenta` | ✅ `/cuenta` | ✅ `/cuenta` |

## Notas

- **Ganado**: Los tres roles pueden acceder (trabajador agrícola incluido).
- **Tratamientos**: El administrador puede ver lo asignado y verificar si la tarea se cumplió o no. El veterinario gestiona tratamientos.
- **Búsquedas**: Todos los usuarios autenticados pueden usar búsquedas.
- **Actividades**: Veterinarios y trabajadores ven qué hay pendiente; el veterinario puede asignar tareas y marcar como efectuadas; el trabajador ve y marca sus tareas.
- **Mi cuenta**: Vista en `/cuenta` con datos del perfil (nombre, correo, teléfono, rol) y enlace a **Cambiar contraseña** (`/cuenta/cambiar-password`).
- Las rutas más específicas en `SecurityConfig` van **antes** de `/admin/**` y `/vet/**` para que cada rol tenga solo lo permitido.
