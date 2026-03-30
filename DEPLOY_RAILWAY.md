# Desplegar AgroSoft CRUD en Railway

Guía para subir el proyecto a [Railway](https://railway.app) y dejarlo en producción.

---

## Requisitos

- Cuenta en [Railway](https://railway.app) (puedes usar GitHub para iniciar sesión).
- Repositorio del proyecto en GitHub (ya conectado: `https://github.com/kamiloo1/agrostf`).

---

## Paso 1: Crear proyecto en Railway

1. Entra en **https://railway.app** e inicia sesión.
2. Clic en **"New Project"**.
3. Elige **"Deploy from GitHub repo"**.
4. Conecta tu cuenta de GitHub si aún no lo has hecho y selecciona el repositorio **`kamiloo1/agrostf`** (o el nombre que tenga tu repo).
5. Railway creará un **servicio** con tu código y empezará a construir la app.

---

## Paso 2: Añadir base de datos MySQL

1. En el proyecto de Railway, clic en **"+ New"** (o **Ctrl+K** / **Cmd+K**).
2. Elige **"Database"** → **"Add MySQL"** (o busca "MySQL" en plantillas).
3. Se desplegará un servicio MySQL. Espera a que esté en estado **Active**.

### Si sale **"Equipo no encontrado"** al dar a MySQL

Ese error suele aparecer cuando la cuenta no tiene un **Team** (equipo) o el proyecto no está bien vinculado. Prueba:

1. **Crear un Team:** menú de Railway (abajo a la izquierda o en tu avatar) → **"Create Team"** o **"New Team"**. Crea un equipo (aunque sea solo para ti), vuelve al proyecto y asegúrate de que el proyecto pertenece a ese equipo. Luego intenta de nuevo **+ New** → **MySQL**.
2. **Usar MySQL externo:** si sigue fallando, puedes usar una base MySQL externa (por ejemplo [PlanetScale](https://planetscale.com) o [Aiven](https://aiven.io) en plan gratuito). En ese caso **no añadas** MySQL en Railway; en el Paso 3, en lugar de referenciar variables, añade en tu servicio de app estas variables con los datos de tu MySQL externo:
   - `SPRING_DATASOURCE_URL` = `jdbc:mysql://HOST:PUERTO/NOMBRE_BD?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true`
   - `SPRING_DATASOURCE_USERNAME` = usuario
   - `SPRING_DATASOURCE_PASSWORD` = contraseña  
   La app ya está preparada para leer estas variables en Railway (perfil `railway`).

---

## Paso 3: Conectar la app con MySQL

1. Entra en el **servicio de tu aplicación** (el que viene del repo, no el de MySQL).
2. Ve a la pestaña **"Variables"**.
3. Clic en **"Add variable"** o **"New Variable"**.
4. Añade esta variable para activar el perfil de Railway:
   - **Nombre:** `SPRING_PROFILES_ACTIVE`
   - **Valor:** `railway`
5. Referenciar las variables del servicio MySQL:
   - Clic en **"Add a variable reference"** o **"Reference"**.
   - Elige el **servicio MySQL** que creaste.
   - Añade referencias a: **MYSQLHOST**, **MYSQLPORT**, **MYSQLDATABASE**, **MYSQLUSER**, **MYSQLPASSWORD**.
   - O bien, en Variables, crea variables que referencien al servicio MySQL (Railway suele mostrar algo como `${{MySQL.MYSQLHOST}}` según la interfaz).

   Si tu versión de Railway usa “Variable references”:
   - Crea variables en tu servicio de app con **nombre** = `MYSQLHOST` y **valor** = referencia al servicio MySQL → variable `MYSQLHOST` (y lo mismo para `MYSQLPORT`, `MYSQLDATABASE`, `MYSQLUSER`, `MYSQLPASSWORD`).

   Así la app usará automáticamente la URL JDBC construida en `application-railway.properties` con esas variables.

---

## Paso 4: Correo en Railway (importante)

En planes **Free / Trial / Hobby**, Railway suele **bloquear SMTP saliente** (puertos 587/465).  
Por eso **Gmail por `smtp.gmail.com` puede dar timeout** aunque la contraseña sea correcta.

**Recomendado:** [Resend](https://resend.com) por **HTTPS** (API). Crea una cuenta, genera una **API key** y en Variables de tu servicio añade:

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `RESEND_API_KEY` | API key de Resend | `re_...` |
| `RESEND_FROM` | Remitente (pruebas o dominio verificado) | `onboarding@resend.dev` o `notificaciones@tudominio.com` |

Opcional: mismo servicio, variables de clima y SMTP (solo si tu plan permite SMTP o usas otro host):

| Variable | Descripción |
|----------|-------------|
| `APP_CLIMA_API_KEY` | OpenWeatherMap |
| `SPRING_MAIL_USERNAME` / `SPRING_MAIL_PASSWORD` | Solo si usas SMTP y no Resend |

Sin `RESEND_API_KEY` ni credenciales SMTP válidas, el envío de correo no funcionará.

---

## Paso 5: Dominio público

1. En el **servicio de tu aplicación**, ve a la pestaña **"Settings"**.
2. Busca la sección **"Networking"** o **"Public networking"**.
3. Clic en **"Generate domain"**.
4. Railway te dará una URL tipo: `tu-app.up.railway.app`.

Al desplegar de nuevo, esa URL mostrará tu aplicación.

---

## Paso 6: Despliegue

- Si desplegaste desde GitHub, cada **push** a la rama que Railway vigila (p. ej. `main`) puede disparar un nuevo despliegue.
- También puedes forzar un **redeploy** desde el panel de Railway en tu servicio (botón **"Redeploy"** o **"Deploy"**).

Tras el build, revisa la pestaña **"Logs"** para confirmar que Spring Boot arranca y se conecta a MySQL.

---

## Resumen de variables en Railway (servicio app)

| Variable | Obligatoria | Descripción |
|----------|-------------|-------------|
| `SPRING_PROFILES_ACTIVE` | Sí | `railway` |
| `MYSQLHOST` | Sí* | Referencia al servicio MySQL |
| `MYSQLPORT` | Sí* | Referencia al servicio MySQL |
| `MYSQLDATABASE` | Sí* | Referencia al servicio MySQL |
| `MYSQLUSER` | Sí* | Referencia al servicio MySQL |
| `MYSQLPASSWORD` | Sí* | Referencia al servicio MySQL |
| `SPRING_MAIL_USERNAME` | No | Correo Gmail |
| `SPRING_MAIL_PASSWORD` | No | Contraseña de aplicación Gmail |
| `APP_CLIMA_API_KEY` | No | API key OpenWeatherMap |

\* O bien configurar una sola URL con `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` y `SPRING_DATASOURCE_PASSWORD` si usas otra fuente de MySQL.

---

## Qué hace el perfil `railway`

- **Puerto:** usa la variable `PORT` que Railway asigna (necesaria para que el proxy enrute a tu app).
- **Base de datos:** usa `MYSQLHOST`, `MYSQLPORT`, `MYSQLDATABASE`, `MYSQLUSER`, `MYSQLPASSWORD` (o las `SPRING_DATASOURCE_*` si las defines).
- **Producción:** cache de Thymeleaf activado, menos logs en consola, cookies de sesión en modo seguro para HTTPS.

Los archivos de configuración están en:

- `src/main/resources/application.properties` → desarrollo local.
- `src/main/resources/application-railway.properties` → solo cuando `SPRING_PROFILES_ACTIVE=railway`.

---

## Problemas frecuentes

- **La app no arranca:** revisa los logs en Railway. Suele ser falta de `SPRING_PROFILES_ACTIVE=railway` o variables de MySQL mal referenciadas.
- **Error de conexión a MySQL:** comprueba que el servicio MySQL está activo y que en tu servicio de app tienes las variables/referencias de MySQL correctas.
- **Puerto:** no definas `PORT` a mano; Railway la inyecta. El perfil `railway` ya usa `server.port=${PORT:8085}`.

Si quieres, el siguiente paso puede ser desplegar desde tu repo en GitHub siguiendo estos pasos en el panel de Railway.
