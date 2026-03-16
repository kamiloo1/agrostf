@echo off
chcp 65001 >nul
cd /d "%~dp0"
set "GIT_EXE=C:\Program Files\Git\bin\git.exe"

echo Cierra MANUAL_TECNICO_AGROSOFT.html si lo tienes abierto (Word, navegador, etc.).
echo Luego pulsa una tecla para continuar...
pause >nul

echo.
echo Comprobando estado de Git...
"%GIT_EXE%" status
echo.

if exist .git\rebase-merge (
    echo Terminando rebase...
    set GIT_EDITOR=true
    "%GIT_EXE%" rebase --continue
    if errorlevel 1 (
        echo Si vuelve a fallar por MANUAL_TECNICO_AGROSOFT.html, ciérralo y ejecuta de nuevo este script.
        pause
        exit /b 1
    )
)

echo.
echo Subiendo a GitHub...
"%GIT_EXE%" push -u origin main

if %errorlevel% equ 0 (
    echo.
    echo Listo. Proyecto subido a https://github.com/kamiloo1/agrostf
) else (
    echo.
    echo Si pide usuario/contraseña: usuario de GitHub y Personal Access Token como contraseña.
)
echo.
pause
