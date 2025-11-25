@echo off
echo ========================================
echo VERIFICACION DE CONFIGURACION - AgroSoft
echo ========================================
echo.

echo [1] Verificando puerto 8085...
netstat -ano | findstr :8085 >nul
if %errorlevel% == 0 (
    echo    [X] Puerto 8085 esta en uso
    echo    [!] Necesitas cambiar el puerto en application.properties o terminar el proceso
    netstat -ano | findstr :8085
) else (
    echo    [OK] Puerto 8085 esta disponible
)
echo.

echo [2] Verificando MySQL...
sc query MySQL80 >nul 2>&1
if %errorlevel% == 0 (
    echo    [OK] Servicio MySQL encontrado
    sc query MySQL80 | findstr "STATE"
) else (
    echo    [!] Servicio MySQL no encontrado o con otro nombre
    echo    [!] Verifica manualmente que MySQL este corriendo
)
echo.

echo [3] Verificando archivos de configuracion...
if exist "src\main\resources\application.properties" (
    echo    [OK] application.properties existe
    findstr "agrosft" "src\main\resources\application.properties" >nul
    if %errorlevel% == 0 (
        echo    [OK] Base de datos configurada como 'agrosft'
    ) else (
        echo    [X] Base de datos NO configurada como 'agrosft'
    )
) else (
    echo    [X] application.properties NO existe
)
echo.

if exist "setup_database.sql" (
    echo    [OK] setup_database.sql existe
) else (
    echo    [X] setup_database.sql NO existe
)
echo.

echo [4] Verificando Java...
java -version >nul 2>&1
if %errorlevel% == 0 (
    echo    [OK] Java esta instalado
    java -version
) else (
    echo    [X] Java NO esta instalado o no esta en PATH
)
echo.

echo [5] Verificando Maven...
mvn -version >nul 2>&1
if %errorlevel% == 0 (
    echo    [OK] Maven esta instalado
    mvn -version | findstr "Apache Maven"
) else (
    echo    [X] Maven NO esta instalado o no esta en PATH
)
echo.

echo ========================================
echo Verificacion completada
echo ========================================
echo.
echo PROXIMOS PASOS:
echo 1. Si el puerto 8085 esta ocupado, cambiar en application.properties
echo 2. Asegurar que MySQL este corriendo
echo 3. Ejecutar: mysql -u root -p ^< setup_database.sql
echo 4. Compilar: mvn clean package
echo 5. Ejecutar: java -jar target\Agrosotf-crud-0.0.1-SNAPSHOT.jar
echo.
pause

