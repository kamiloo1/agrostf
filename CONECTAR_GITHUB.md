# Conectar el proyecto AgroSoft CRUD con GitHub

Ya tienes cuenta en GitHub. Sigue estos pasos para subir tu proyecto.

---

## Opción rápida: script automático

En la carpeta del proyecto hay un archivo **`conectar-github.bat`**.

1. **Instala Git** (Paso 1 abajo) si aún no lo tienes.
2. **Crea el repositorio** en GitHub (Paso 2) y copia su URL.
3. **Doble clic** en `conectar-github.bat`.
4. Cuando pida la URL, pega la de tu repo (ej: `https://github.com/TU_USUARIO/agrosoft-crud.git`).
5. Si `git push` pide contraseña, usa tu **Personal Access Token** (ver Paso 3).

Si prefieres hacerlo a mano, sigue los pasos siguientes.

---

## Paso 1: Instalar Git en tu PC (si no lo tienes)

1. Descarga **Git para Windows**: https://git-scm.com/download/win  
2. Instala con las opciones por defecto (incluye añadir `git` al PATH).  
3. Cierra y vuelve a abrir la terminal o Cursor para que reconozca `git`.

Para comprobar que está instalado, abre una terminal y escribe:
```bash
git --version
```

---

## Paso 2: Crear un repositorio en tu cuenta de GitHub

1. Entra en **https://github.com** e inicia sesión con tu cuenta.  
2. Clic en **"+"** (arriba a la derecha) → **"New repository"**.  
3. **Repository name:** por ejemplo `agrosoft-crud` o `AgroSoft-CRUD`.  
4. **Description:** (opcional) "Sistema de gestión agropecuaria - Spring Boot".  
5. Elige **Public**.  
6. **No** marques "Add a README file" (ya tienes código local).  
7. Clic en **"Create repository"**.  
8. Copia la URL del repositorio (ej: `https://github.com/TU_USUARIO/agrosoft-crud.git`).

---

## Paso 3: Inicializar Git y conectar con GitHub

Abre una terminal (PowerShell o CMD) en la carpeta del proyecto:

```bash
cd "c:\xampp\htdocs\agroft-crud fseana\agrosoft-crud final"
```

Luego ejecuta estos comandos **uno por uno** (sustituye `TU_USUARIO` y `agrosoft-crud` por tu usuario y nombre del repo):

```bash
git init
git add .
git commit -m "Initial commit: AgroSoft CRUD - Spring Boot"
git branch -M main
git remote add origin https://github.com/TU_USUARIO/agrosoft-crud.git
git push -u origin main
```

- **git init** – Crea el repositorio Git en la carpeta.  
- **git add .** – Añade todos los archivos (el `.gitignore` excluye `target/`, `.idea/`, etc.).  
- **git commit** – Crea el primer commit.  
- **git branch -M main** – Usa la rama `main`.  
- **git remote add origin** – Enlaza con tu repo de GitHub.  
- **git push** – Sube el código a GitHub.

La primera vez que hagas `git push`, Git puede pedirte **usuario y contraseña**. En GitHub ya no se usa contraseña normal; debes usar un **Personal Access Token (PAT)**:

1. En GitHub: **Settings** (tu perfil) → **Developer settings** → **Personal access tokens** → **Tokens (classic)**.  
2. **Generate new token (classic)**.  
3. Pon un nombre (ej: "AgroSoft") y marca al menos el permiso **repo**.  
4. Genera y **copia el token** (solo se muestra una vez).  
5. Cuando `git push` pida contraseña, **pega el token** (no tu contraseña de GitHub).

---

## Paso 4: Comprobar

Entra en tu repositorio en GitHub en el navegador. Deberías ver todo el código del proyecto.

---

## Comandos útiles después de conectar

| Acción | Comando |
|--------|--------|
| Ver estado | `git status` |
| Añadir cambios | `git add .` o `git add nombre_archivo` |
| Hacer commit | `git commit -m "Descripción del cambio"` |
| Subir a GitHub | `git push` |
| Bajar cambios | `git pull` |

---

## Si ya tienes Git instalado

Si Git ya está en tu sistema pero la terminal no lo reconoce, añade la ruta de Git al PATH de Windows (suele ser `C:\Program Files\Git\cmd`). O abre **"Git Bash"** desde el menú de inicio y ejecuta los comandos del Paso 3 desde la carpeta del proyecto.
