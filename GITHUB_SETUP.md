# 🚀 Guía para Subir el Proyecto a GitHub

## 📋 Pasos para Subir el Proyecto

### Paso 1: Crear Repositorio en GitHub

1. Ve a [GitHub](https://github.com)
2. Haz clic en el botón **"New"** o **"+"** → **"New repository"**
3. Configura el repositorio:
   - **Repository name**: `agrosoft-crud1` (o el nombre que prefieras)
   - **Description**: "Sistema de Gestión Agropecuaria desarrollado en Spring Boot"
   - **Visibility**: Public o Private (según prefieras)
   - **NO marques** "Initialize this repository with a README" (ya tenemos uno)
4. Haz clic en **"Create repository"**

### Paso 2: Configurar Git Local (si no está configurado)

```bash
# Configurar tu nombre de usuario (si no lo has hecho)
git config --global user.name "Tu Nombre"
git config --global user.email "tu.email@ejemplo.com"
```

### Paso 3: Agregar Archivos y Hacer Commit Inicial

```bash
# Agregar todos los archivos
git add .

# Hacer commit inicial
git commit -m "Initial commit: Proyecto AgroSoft completo con Spring Boot 3.3.4"
```

### Paso 4: Crear Ramas

```bash
# Crear rama de desarrollo
git checkout -b develop

# Crear rama de features
git checkout -b feature/documentation

# Volver a main
git checkout main
```

### Paso 5: Conectar con GitHub y Subir

```bash
# Agregar el repositorio remoto (reemplaza USERNAME con tu usuario de GitHub)
git remote add origin https://github.com/USERNAME/agrosoft-crud1.git

# O si prefieres SSH:
# git remote add origin git@github.com:USERNAME/agrosoft-crud1.git

# Verificar que se agregó correctamente
git remote -v

# Subir la rama main
git push -u origin main

# Subir la rama develop
git checkout develop
git push -u origin develop
```

## 🌿 Estructura de Ramas Recomendada

### Ramas Principales:
- **main** (master): Código en producción
- **develop**: Código en desarrollo

### Ramas de Trabajo:
- **feature/nombre-feature**: Para nuevas funcionalidades
- **bugfix/nombre-bug**: Para corrección de bugs
- **hotfix/nombre-hotfix**: Para correcciones urgentes

## 📝 Comandos Útiles

### Crear y Cambiar de Rama:
```bash
# Crear nueva rama
git checkout -b feature/nueva-funcionalidad

# Cambiar de rama
git checkout main
git checkout develop
```

### Trabajar con Ramas:
```bash
# Ver todas las ramas
git branch -a

# Subir una rama nueva
git push -u origin nombre-rama

# Eliminar rama local
git branch -d nombre-rama

# Eliminar rama remota
git push origin --delete nombre-rama
```

### Actualizar desde GitHub:
```bash
# Descargar cambios
git fetch origin

# Actualizar rama actual
git pull origin main
```

## 🔐 Autenticación con GitHub

### Opción 1: Personal Access Token (Recomendado)
1. GitHub → Settings → Developer settings → Personal access tokens → Tokens (classic)
2. Generate new token
3. Selecciona permisos: `repo`
4. Copia el token y úsalo como contraseña cuando hagas `git push`

### Opción 2: SSH Keys
```bash
# Generar clave SSH (si no tienes)
ssh-keygen -t ed25519 -C "tu.email@ejemplo.com"

# Copiar clave pública
cat ~/.ssh/id_ed25519.pub

# Agregar en GitHub: Settings → SSH and GPG keys → New SSH key
```

## 📋 Checklist Pre-Subida

- [ ] `.gitignore` configurado correctamente
- [ ] No hay archivos sensibles (passwords, keys) en el código
- [ ] README.md actualizado
- [ ] Repositorio creado en GitHub
- [ ] Git configurado localmente
- [ ] Commit inicial realizado
- [ ] Ramas creadas
- [ ] Repositorio remoto agregado
- [ ] Push realizado

## ⚠️ Archivos que NO deben subirse

- `application.properties` con passwords reales (usar variables de entorno)
- Archivos `.jar` compilados
- Carpetas `target/`
- Archivos de IDE (`.idea`, `.vscode`, etc.)

## 🎯 Comandos Rápidos (Todo en Uno)

```bash
# 1. Agregar archivos
git add .

# 2. Commit inicial
git commit -m "Initial commit: Proyecto AgroSoft completo"

# 3. Crear rama develop
git checkout -b develop

# 4. Agregar remoto (reemplaza USERNAME)
git remote add origin https://github.com/USERNAME/agrosoft-crud1.git

# 5. Subir main
git checkout main
git push -u origin main

# 6. Subir develop
git checkout develop
git push -u origin develop
```

## 📚 Recursos Adicionales

- [GitHub Docs](https://docs.github.com)
- [Git Handbook](https://guides.github.com/introduction/git-handbook/)
- [Git Flow](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow)

