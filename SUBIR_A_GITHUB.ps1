# Script para subir proyecto a GitHub
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "SUBIR PROYECTO A GITHUB - AgroSoft" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Verificar estado
Write-Host "[1] Verificando estado de Git..." -ForegroundColor Yellow
git status
Write-Host ""

# Verificar ramas
Write-Host "[2] Verificando ramas locales..." -ForegroundColor Yellow
git branch
Write-Host ""

# Verificar remoto
Write-Host "[3] Verificando repositorio remoto..." -ForegroundColor Yellow
$remote = git remote -v
if ($remote) {
    Write-Host "    Repositorio remoto configurado:" -ForegroundColor Green
    $remote
} else {
    Write-Host "    [X] NO hay repositorio remoto configurado" -ForegroundColor Red
    Write-Host ""
    Write-Host "    Necesitas:" -ForegroundColor Yellow
    Write-Host "    1. Crear repositorio en GitHub" -ForegroundColor White
    Write-Host "    2. Ejecutar: git remote add origin https://github.com/USUARIO/agrosoft-crud1.git" -ForegroundColor White
    Write-Host "    3. Ejecutar: git push -u origin main" -ForegroundColor White
}
Write-Host ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "INSTRUCCIONES COMPLETAS:" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "1. Crear repositorio en GitHub:" -ForegroundColor Yellow
Write-Host "   - Ve a https://github.com" -ForegroundColor White
Write-Host "   - Clic en 'New' o '+' > 'New repository'" -ForegroundColor White
Write-Host "   - Nombre: agrosoft-crud1" -ForegroundColor White
Write-Host "   - NO marques 'Initialize with README'" -ForegroundColor White
Write-Host "   - Clic en 'Create repository'" -ForegroundColor White
Write-Host ""
Write-Host "2. Conectar y subir:" -ForegroundColor Yellow
Write-Host "   git remote add origin https://github.com/USUARIO/agrosoft-crud1.git" -ForegroundColor Green
Write-Host "   git push -u origin main" -ForegroundColor Green
Write-Host "   git checkout develop" -ForegroundColor Green
Write-Host "   git push -u origin develop" -ForegroundColor Green
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan

