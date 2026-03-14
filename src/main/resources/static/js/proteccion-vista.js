/**
 * Script de Protección de Vistas - AgroSoft
 * Protege contra copiar, pegar, capturas de pantalla y acceso no autorizado
 */

(function() {
    'use strict';

    // ============================================
    // PROTECCIÓN CONTRA COPIAR Y PEGAR
    // ============================================
    
    // Deshabilitar clic derecho (menú contextual)
    document.addEventListener('contextmenu', function(e) {
        e.preventDefault();
        mostrarAlerta('No se permite el clic derecho en esta página.');
        return false;
    });

    // Deshabilitar selección de texto
    document.addEventListener('selectstart', function(e) {
        e.preventDefault();
        return false;
    });

    // Deshabilitar arrastrar y soltar
    document.addEventListener('dragstart', function(e) {
        e.preventDefault();
        return false;
    });

    // Deshabilitar copiar (Ctrl+C, Ctrl+A, etc.)
    document.addEventListener('keydown', function(e) {
        // Bloquear Ctrl+C, Ctrl+A, Ctrl+X, Ctrl+V, Ctrl+S, Ctrl+P, F12
        if (e.ctrlKey && (e.key === 'c' || e.key === 'C' || 
                         e.key === 'v' || e.key === 'V' || 
                         e.key === 'x' || e.key === 'X' || 
                         e.key === 'a' || e.key === 'A' || 
                         e.key === 's' || e.key === 'S' || 
                         e.key === 'p' || e.key === 'P')) {
            e.preventDefault();
            mostrarAlerta('Esta acción no está permitida por seguridad.');
            return false;
        }
        
        // Bloquear F12 (DevTools)
        if (e.key === 'F12' || (e.ctrlKey && e.shiftKey && (e.key === 'I' || e.key === 'J' || e.key === 'C'))) {
            e.preventDefault();
            mostrarAlerta('Las herramientas de desarrollador están deshabilitadas.');
            return false;
        }
        
        // Bloquear Ctrl+Shift+I, Ctrl+Shift+J, Ctrl+Shift+C
        if (e.ctrlKey && e.shiftKey) {
            e.preventDefault();
            return false;
        }
    });

    // Deshabilitar pegar
    document.addEventListener('paste', function(e) {
        e.preventDefault();
        mostrarAlerta('No se permite pegar contenido en esta página.');
        return false;
    });

    // Deshabilitar copiar
    document.addEventListener('copy', function(e) {
        e.preventDefault();
        mostrarAlerta('No se permite copiar contenido de esta página.');
        return false;
    });

    // Deshabilitar cortar
    document.addEventListener('cut', function(e) {
        e.preventDefault();
        mostrarAlerta('No se permite cortar contenido de esta página.');
        return false;
    });

    // ============================================
    // PROTECCIÓN CONTRA CAPTURAS DE PANTALLA
    // ============================================
    
    // Bloquear Print Screen
    document.addEventListener('keydown', function(e) {
        if (e.key === 'PrintScreen') {
            e.preventDefault();
            mostrarAlerta('No se permite capturar pantalla.');
            return false;
        }
    });

    // ============================================
    // PROTECCIÓN CONTRA HERRAMIENTAS DE DESARROLLADOR
    // ============================================
    
    // Detectar apertura de DevTools
    let devtools = {open: false, orientation: null};
    const threshold = 160;
    
    setInterval(function() {
        if (window.outerHeight - window.innerHeight > threshold || 
            window.outerWidth - window.innerWidth > threshold) {
            if (!devtools.open) {
                devtools.open = true;
                mostrarAlerta('Las herramientas de desarrollador están deshabilitadas.');
                // Opcional: redirigir a login
                // window.location.href = '/login?error=devtools';
            }
        } else {
            devtools.open = false;
        }
    }, 500);

    // ============================================
    // PROTECCIÓN CONTRA DESACTIVAR JAVASCRIPT
    // ============================================
    
    // Detectar si JavaScript está deshabilitado (solo cuando body exista)
    if (document.body) {
        document.body.setAttribute('data-js-enabled', 'true');
    } else {
        document.addEventListener('DOMContentLoaded', function() {
            if (document.body) document.body.setAttribute('data-js-enabled', 'true');
        });
    }

    // ============================================
    // VERIFICACIÓN DE SESIÓN ACTIVA (cierre automático por inactividad)
    // ============================================
    
    let ultimaActividad = Date.now();
    let TIMEOUT_SESION = 30 * 60 * 1000; // Por defecto 30 minutos
    
    // Obtener timeout desde el servidor (sincronizado con application.properties)
    fetch('/api/session-config')
        .then(function(r) { return r.ok ? r.json() : null; })
        .then(function(config) {
            if (config && config.timeoutMs) TIMEOUT_SESION = config.timeoutMs;
        })
        .catch(function() { /* Usar valor por defecto */ });
    
    // Detectar actividad del usuario
    ['mousedown', 'mousemove', 'keypress', 'scroll', 'touchstart'].forEach(function(evento) {
        document.addEventListener(evento, function() {
            ultimaActividad = Date.now();
        }, { passive: true });
    });
    
    // Verificar sesión cada minuto
    setInterval(function() {
        const tiempoInactivo = Date.now() - ultimaActividad;
        if (tiempoInactivo > TIMEOUT_SESION) {
            mostrarAlerta('Tu sesión ha expirado por inactividad. Serás redirigido al login.');
            setTimeout(function() {
                window.location.href = '/login?expired=true';
            }, 2000);
        }
    }, 60000); // Verificar cada minuto

    // ============================================
    // FUNCIÓN PARA MOSTRAR ALERTAS
    // ============================================
    
    function mostrarAlerta(mensaje) {
        // Crear elemento de alerta si no existe
        let alerta = document.getElementById('alerta-seguridad');
        if (!alerta) {
            alerta = document.createElement('div');
            alerta.id = 'alerta-seguridad';
            alerta.style.cssText = `
                position: fixed;
                top: 20px;
                right: 20px;
                background: #dc3545;
                color: white;
                padding: 15px 20px;
                border-radius: 8px;
                box-shadow: 0 4px 12px rgba(0,0,0,0.3);
                z-index: 10000;
                font-family: Arial, sans-serif;
                font-size: 14px;
                max-width: 300px;
                animation: slideIn 0.3s ease;
            `;
            document.body.appendChild(alerta);
            
            // Agregar animación
            const style = document.createElement('style');
            style.textContent = `
                @keyframes slideIn {
                    from { transform: translateX(400px); opacity: 0; }
                    to { transform: translateX(0); opacity: 1; }
                }
            `;
            document.head.appendChild(style);
        }
        
        alerta.textContent = mensaje;
        alerta.style.display = 'block';
        
        // Ocultar después de 3 segundos
        setTimeout(function() {
            alerta.style.display = 'none';
        }, 3000);
    }

    // ============================================
    // PROTECCIÓN CONTRA NAVEGACIÓN ATRÁS Y ADELANTE
    // ============================================
    
    // Evitar que se muestre la página desde caché: redirigir al login de inmediato
    function redirigirALogin() {
        window.location.replace('/login?expired=nav');
    }
    
    // Cuando el usuario usa Atrás/Adelante y el navegador restaura la página desde caché (bfcache),
    // no mostrar esa página: pedir ingreso de nuevo de inmediato
    window.addEventListener('pageshow', function(event) {
        if (event.persisted) {
            redirigirALogin();
        }
    });
    
    // Cuando el usuario pulsa Atrás o Adelante (popstate), no recargar la página: pedir ingreso de nuevo
    history.pushState(null, null, location.href);
    window.addEventListener('popstate', function() {
        redirigirALogin();
    });

    // ============================================
    // PROTECCIÓN CONTRA IFRAMES (Clickjacking)
    // ============================================
    
    if (window.top !== window.self) {
        window.top.location = window.self.location;
    }

    // ============================================
    // ESTILOS PARA DESHABILITAR SELECCIÓN VISUAL
    // ============================================
    
    const estiloProteccion = document.createElement('style');
    estiloProteccion.textContent = `
        * {
            -webkit-user-select: none !important;
            -moz-user-select: none !important;
            -ms-user-select: none !important;
            user-select: none !important;
            -webkit-touch-callout: none !important;
            -webkit-tap-highlight-color: transparent !important;
        }
        
        input, textarea, select {
            -webkit-user-select: text !important;
            -moz-user-select: text !important;
            -ms-user-select: text !important;
            user-select: text !important;
        }
    `;
    document.head.appendChild(estiloProteccion);
})();

