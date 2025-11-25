# HISTORIAS DE USUARIO - SISTEMA AGROSOFT

## Historia de Usuario #1: Registro de Usuarios con Validación

**Título:** Como administrador, quiero registrar nuevos usuarios en el sistema con validación de datos para garantizar la integridad de la información.

**Descripción:**  
El administrador necesita poder registrar nuevos usuarios (veterinarios, trabajadores) en el sistema. El sistema debe validar que no haya correos duplicados, que los campos obligatorios estén completos y que el formato de los datos sea correcto.

**Criterios de Aceptación:**
- ✅ El sistema valida que el correo electrónico no esté duplicado
- ✅ El sistema valida que el número de documento no esté duplicado
- ✅ El sistema valida el formato del correo electrónico
- ✅ El sistema requiere campos obligatorios: correo y contraseña
- ✅ El sistema muestra mensajes de error claros cuando hay validaciones fallidas
- ✅ Solo el administrador puede gestionar usuarios

**Prioridad:** Alta

**Flujo:**
1. El administrador accede a la sección de gestión de usuarios
2. Completa el formulario de registro
3. El sistema valida los datos ingresados
4. Si hay errores, muestra mensajes específicos
5. Si todo es correcto, guarda el usuario y muestra mensaje de éxito

---

## Historia de Usuario #2: Creación de Tratamientos con Validación de Paciente

**Título:** Como veterinario, quiero crear tratamientos para pacientes existentes para registrar el historial médico.

**Descripción:**  
Los veterinarios necesitan registrar tratamientos médicos aplicados a los animales (pacientes). El sistema debe garantizar que no se puedan crear tratamientos sin un paciente asociado.

**Criterios de Aceptación:**
- ✅ El sistema valida que existe un paciente (ganado) antes de crear el tratamiento
- ✅ El sistema requiere campos obligatorios: tipo de tratamiento y fecha
- ✅ El sistema muestra error si se intenta crear tratamiento sin paciente
- ✅ El veterinario puede ver la lista de pacientes disponibles
- ✅ El tratamiento se asocia correctamente al paciente seleccionado

**Prioridad:** Alta

**Flujo:**
1. El veterinario accede a la sección de tratamientos
2. Selecciona "Crear nuevo tratamiento"
3. El sistema muestra lista de pacientes disponibles
4. El veterinario selecciona un paciente
5. Completa los datos del tratamiento
6. El sistema valida que el paciente existe
7. Guarda el tratamiento y muestra confirmación

---

## Historia de Usuario #3: Eliminación de Pacientes con Restricción de Tratamientos

**Título:** Como administrador, quiero eliminar pacientes del sistema, pero el sistema debe prevenir la eliminación si tienen tratamientos activos.

**Descripción:**  
Los administradores necesitan poder eliminar pacientes del sistema, pero por integridad de datos, no se deben eliminar pacientes que tengan tratamientos asociados.

**Criterios de Aceptación:**
- ✅ El sistema verifica si el paciente tiene tratamientos antes de eliminar
- ✅ Si tiene tratamientos, muestra un error y no permite la eliminación
- ✅ Si no tiene tratamientos, permite la eliminación
- ✅ El mensaje de error indica cuántos tratamientos tiene el paciente
- ✅ El sistema mantiene la integridad referencial de los datos

**Prioridad:** Media

**Flujo:**
1. El administrador intenta eliminar un paciente
2. El sistema verifica tratamientos asociados
3. Si hay tratamientos, muestra error y cancela la operación
4. Si no hay tratamientos, confirma y elimina el paciente
5. Muestra mensaje de éxito o error según corresponda

---

## Historia de Usuario #4: Envío Masivo de Correos

**Título:** Como administrador, quiero enviar correos masivos a múltiples destinatarios para notificaciones y campañas.

**Descripción:**  
El administrador necesita poder enviar correos electrónicos a múltiples usuarios del sistema para alertas, campañas o informes.

**Criterios de Aceptación:**
- ✅ El sistema permite ingresar múltiples correos (separados por comas o saltos de línea)
- ✅ El sistema valida el formato de cada correo
- ✅ El sistema permite ingresar asunto y mensaje personalizado
- ✅ El sistema envía correos a todos los destinatarios válidos
- ✅ El sistema muestra el número de correos enviados exitosamente
- ✅ El sistema maneja errores de envío de forma elegante

**Prioridad:** Media

**Flujo:**
1. El administrador accede a la sección de envío masivo de correos
2. Ingresa la lista de correos destinatarios
3. Ingresa el asunto y mensaje
4. Hace clic en "Enviar correos"
5. El sistema valida y envía los correos
6. Muestra resumen de envíos exitosos y fallidos

---

## Historia de Usuario #5: Generación de Reportes Estadísticos en PDF

**Título:** Como administrador, quiero generar reportes estadísticos en PDF para análisis y documentación.

**Descripción:**  
Los administradores necesitan generar reportes en PDF con estadísticas del sistema: total de pacientes, tratamientos, tratamientos activos/inactivos, y gráficas o tablas.

**Criterios de Aceptación:**
- ✅ El sistema genera un PDF con estadísticas completas
- ✅ El PDF incluye: total de pacientes, total de tratamientos, tratamientos activos/inactivos
- ✅ El PDF incluye tablas con datos desglosados (por tipo de paciente, tipo de tratamiento)
- ✅ El PDF se puede descargar desde el navegador
- ✅ El PDF se guarda automáticamente en la carpeta "reportes"
- ✅ El PDF tiene formato profesional con fecha de generación

**Prioridad:** Alta

**Flujo:**
1. El administrador accede a la sección de reportes
2. Hace clic en "Generar PDF"
3. El sistema genera el reporte con todas las estadísticas
4. El PDF se descarga automáticamente
5. El PDF también se guarda en la carpeta "reportes" del servidor

---

## Historia de Usuario #6: Visualización del Clima Actual

**Título:** Como usuario del sistema, quiero ver el clima actual de mi ubicación para planificar actividades agrícolas.

**Descripción:**  
Los usuarios necesitan conocer las condiciones climáticas actuales para tomar decisiones sobre riegos, tratamientos y otras actividades agrícolas.

**Criterios de Aceptación:**
- ✅ El sistema consume una API externa de clima (OpenWeatherMap)
- ✅ El sistema muestra temperatura, descripción, humedad y velocidad del viento
- ✅ El usuario puede buscar el clima de diferentes ciudades
- ✅ El sistema maneja errores de API de forma elegante
- ✅ La información se muestra en una vista amigable

**Prioridad:** Baja

**Flujo:**
1. El usuario accede a la sección de clima
2. Ingresa el nombre de la ciudad (opcional, por defecto Bogotá)
3. El sistema consulta la API del clima
4. Muestra la información del clima actual
5. Si hay error, muestra datos de ejemplo o mensaje informativo

---

## Historia de Usuario #7: Carga Inicial Automática de Datos

**Título:** Como sistema, necesito cargar datos iniciales automáticamente al iniciar por primera vez.

**Descripción:**  
El sistema debe crear automáticamente usuarios de ejemplo (admin, veterinario, trabajador) y datos de prueba (pacientes, tratamientos) cuando se inicia por primera vez.

**Criterios de Aceptación:**
- ✅ El sistema crea un usuario administrador por defecto al iniciar
- ✅ El sistema crea al menos un veterinario y un trabajador de ejemplo
- ✅ El sistema crea pacientes (ganado) de ejemplo
- ✅ El sistema crea tratamientos de ejemplo asociados a pacientes
- ✅ Los datos solo se crean si la base de datos está vacía
- ✅ Los datos se crean usando el patrón Factory para usuarios

**Prioridad:** Media

**Flujo:**
1. El sistema inicia por primera vez
2. El CommandLineRunner verifica si hay datos existentes
3. Si no hay datos, crea roles, usuarios y datos de ejemplo
4. Los usuarios se crean usando el Factory pattern
5. Se registran logs de la creación de datos

---

## Historia de Usuario #8: Control de Acceso por Roles

**Título:** Como sistema, debo controlar el acceso a funcionalidades según el rol del usuario.

**Descripción:**  
El sistema debe restringir el acceso a ciertas funcionalidades según el rol del usuario (administrador, veterinario, trabajador).

**Criterios de Aceptación:**
- ✅ Solo los administradores pueden gestionar usuarios
- ✅ Solo los veterinarios pueden crear y gestionar tratamientos
- ✅ Los trabajadores tienen acceso limitado a sus funcionalidades
- ✅ El sistema redirige según el rol después del login
- ✅ Las rutas están protegidas por Spring Security
- ✅ Los usuarios sin rol no pueden acceder a funcionalidades restringidas

**Prioridad:** Alta

**Flujo:**
1. El usuario inicia sesión
2. El sistema identifica su rol
3. Redirige al dashboard correspondiente
4. Muestra solo las funcionalidades permitidas para su rol
5. Bloquea el acceso a rutas no autorizadas

---

## Historia de Usuario #9: Validación de Formato de Datos

**Título:** Como sistema, debo validar el formato de los datos ingresados para prevenir errores.

**Descripción:**  
El sistema debe validar que los datos ingresados tengan el formato correcto (correos, números, fechas, etc.) antes de guardarlos.

**Criterios de Aceptación:**
- ✅ El sistema valida formato de correo electrónico
- ✅ El sistema valida que las fechas sean válidas
- ✅ El sistema valida que los números estén en rangos aceptables
- ✅ El sistema muestra mensajes de error específicos para cada validación
- ✅ Las validaciones se realizan tanto en frontend como backend

**Prioridad:** Media

**Flujo:**
1. El usuario ingresa datos en un formulario
2. El sistema valida el formato en tiempo real (frontend)
3. Al enviar, el backend valida nuevamente
4. Si hay errores, muestra mensajes específicos
5. Si todo es correcto, procesa y guarda los datos

---

## Historia de Usuario #10: Gestión de Pacientes con Validaciones

**Título:** Como veterinario, quiero gestionar pacientes (ganado) con validaciones para mantener datos consistentes.

**Descripción:**  
Los veterinarios necesitan crear, editar y eliminar pacientes del sistema, pero con validaciones que garanticen la integridad de los datos.

**Criterios de Aceptación:**
- ✅ El sistema valida que el tipo de ganado sea obligatorio
- ✅ El sistema previene la eliminación de pacientes con tratamientos
- ✅ El sistema permite actualizar información de pacientes existentes
- ✅ El sistema muestra lista de pacientes con su estado de salud
- ✅ El sistema permite filtrar pacientes por tipo o estado

**Prioridad:** Alta

**Flujo:**
1. El veterinario accede a la gestión de pacientes
2. Puede crear nuevo paciente completando formulario
3. El sistema valida campos obligatorios
4. Puede editar pacientes existentes
5. Al intentar eliminar, el sistema valida tratamientos asociados
6. Muestra confirmaciones y mensajes de éxito/error

---

## Resumen de Prioridades

- **Alta:** Historias #1, #2, #5, #8, #10
- **Media:** Historias #3, #4, #7, #9
- **Baja:** Historia #6

---

**Fecha de creación:** 2025-01-XX  
**Versión del documento:** 1.0

