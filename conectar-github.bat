@echo off
chcp 65001 >nul
echo ============================================
echo   Conectar AgroSoft CRUD con GitHub
echo ============================================
echo.

where git >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERROR] Git no está instalado o no está en el PATH.
    echo.
    echo 1. Descarga Git: https://git-scm.com/download/win
    echo 2. Instálalo y reinicia esta ventana.
    echo 3. Vuelve a ejecutar este script.
    pause
    exit /b 1
)

cd /d "%~dp0"

if exist .git (
    echo Ya existe un repositorio Git en esta carpeta.
    echo.
) else (
    echo Inicializando repositorio Git...
    git init
    echo.
)

echo Añadiendo archivos...
git add .
echo.

echo Creando primer commit...
git commit -m "Initial commit: AgroSoft CRUD - Spring Boot"
if %errorlevel% neq 0 (
    echo No hay cambios nuevos o ya hay commit. Continuando...
)
echo.

git branch -M main
echo.

set /p URL="Pega la URL de tu repositorio en GitHub (ej: https://github.com/TU_USUARIO/agrosoft-crud.git): "
if "%URL%"=="" (
    echo No se ingresó URL. Para conectar después ejecuta:
    echo   git remote add origin https://github.com/TU_USUARIO/agrosoft-crud.git
    echo   git push -u origin main
    pause
    exit /b 0
)

git remote remove origin 2>nul
git remote add origin %URL%
echo.
echo Subiendo a GitHub...
git push -u origin main

if %errorlevel% equ 0 (
    echo.
    echo ============================================
    echo   Listo. Tu proyecto está en GitHub.
    echo ============================================
) else (
    echo.
    echo Si pide usuario/contraseña: usa tu usuario de GitHub y un Personal Access Token como contraseña.
    echo Crear token: GitHub - Settings - Developer settings - Personal access tokens
)

echo.
pause
