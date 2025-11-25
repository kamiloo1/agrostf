# Script de verificación de configuración para AgroSoft
# PowerShell

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "VERIFICACION DE CONFIGURACION - AgroSoft" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 1. Verificar puerto 8085
Write-Host "[1] Verificando puerto 8085..." -ForegroundColor Yellow
$port = Get-NetTCPConnection -LocalPort 8085 -ErrorAction SilentlyContinue
if ($port) {
    Write-Host "    [X] Puerto 8085 esta en uso" -ForegroundColor Red
    Write-Host "    [!] Necesitas cambiar el puerto en application.properties o terminar el proceso" -ForegroundColor Yellow
    $port | Format-Table -AutoSize
} else {
    Write-Host "    [OK] Puerto 8085 esta disponible" -ForegroundColor Green
}
Write-Host ""

# 2. Verificar MySQL
Write-Host "[2] Verificando MySQL..." -ForegroundColor Yellow
$mysqlService = Get-Service -Name "*MySQL*" -ErrorAction SilentlyContinue
if ($mysqlService) {
    Write-Host "    [OK] Servicio MySQL encontrado:" -ForegroundColor Green
    $mysqlService | Format-Table -AutoSize
} else {
    Write-Host "    [!] Servicio MySQL no encontrado" -ForegroundColor Yellow
    Write-Host "    [!] Verifica manualmente que MySQL este corriendo" -ForegroundColor Yellow
}
Write-Host ""

# 3. Verificar archivos de configuración
Write-Host "[3] Verificando archivos de configuracion..." -ForegroundColor Yellow
$appProps = "src\main\resources\application.properties"
if (Test-Path $appProps) {
    Write-Host "    [OK] application.properties existe" -ForegroundColor Green
    $content = Get-Content $appProps -Raw
    if ($content -match "agrosft") {
        Write-Host "    [OK] Base de datos configurada como 'agrosft'" -ForegroundColor Green
    } else {
        Write-Host "    [X] Base de datos NO configurada como 'agrosft'" -ForegroundColor Red
    }
} else {
    Write-Host "    [X] application.properties NO existe" -ForegroundColor Red
}

if (Test-Path "setup_database.sql") {
    Write-Host "    [OK] setup_database.sql existe" -ForegroundColor Green
} else {
    Write-Host "    [X] setup_database.sql NO existe" -ForegroundColor Red
}
Write-Host ""

# 4. Verificar Java
Write-Host "[4] Verificando Java..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1
    Write-Host "    [OK] Java esta instalado" -ForegroundColor Green
    $javaVersion | Select-Object -First 1
} catch {
    Write-Host "    [X] Java NO esta instalado o no esta en PATH" -ForegroundColor Red
}
Write-Host ""

# 5. Verificar Maven
Write-Host "[5] Verificando Maven..." -ForegroundColor Yellow
try {
    $mavenVersion = mvn -version 2>&1 | Select-Object -First 1
    Write-Host "    [OK] Maven esta instalado" -ForegroundColor Green
    $mavenVersion
} catch {
    Write-Host "    [X] Maven NO esta instalado o no esta en PATH" -ForegroundColor Red
}
Write-Host ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Verificacion completada" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "PROXIMOS PASOS:" -ForegroundColor Yellow
Write-Host "1. Si el puerto 8085 esta ocupado, cambiar en application.properties"
Write-Host "2. Asegurar que MySQL este corriendo"
Write-Host "3. Ejecutar: mysql -u root -p < setup_database.sql"
Write-Host "4. Compilar: mvn clean package"
Write-Host "5. Ejecutar: java -jar target\Agrosotf-crud-0.0.1-SNAPSHOT.jar"
Write-Host ""

