@echo off
echo ========================================
echo SUBIR PROYECTO A GITHUB - AgroSoft
echo ========================================
echo.

echo [1] Verificando estado de Git...
git status
echo.

echo [2] Verificando ramas locales...
git branch
echo.

echo [3] IMPORTANTE: Necesitas crear el repositorio en GitHub primero
echo.
echo Pasos:
echo 1. Ve a https://github.com
echo 2. Clic en "New" o "+" ^> "New repository"
echo 3. Nombre: agrosoft-crud1
echo 4. NO marques "Initialize with README"
echo 5. Clic en "Create repository"
echo.
echo [4] Despues de crear el repositorio, ejecuta estos comandos:
echo.
echo    git remote add origin https://github.com/TU_USUARIO/agrosoft-crud1.git
echo    git push -u origin main
echo    git checkout develop
echo    git push -u origin develop
echo.
echo ========================================
echo Reemplaza TU_USUARIO con tu usuario de GitHub
echo ========================================
pause

