# PATRONES DE DISEÑO GoF (Gang of Four)
## Carpeta dedicada para patrones de diseño implementados

Esta carpeta contiene los patrones de diseño GoF implementados en el sistema AgroSoft.

---

## 📁 Estructura

```
src/main/java/com/example/agrosoft1/crud/gof/
├── README.md (este archivo)
└── UsuarioFactory.java (Patrón Factory)
```

---

## 🔧 Patrones Implementados

### 1. Factory Pattern - UsuarioFactory

**Ubicación:** `UsuarioFactory.java`

**Propósito:** 
Encapsula la lógica de creación de objetos `Usuario` según diferentes tipos (roles: ADMIN, VETERINARIO, TRABAJADOR).

**Cuándo se usa:**
- En `DataInitializer` para crear usuarios iniciales al iniciar el sistema
- Para crear usuarios con configuraciones específicas según su rol

**Beneficios:**
- ✅ Encapsula la lógica de creación compleja
- ✅ Facilita la extensión para nuevos tipos de usuarios
- ✅ Centraliza la configuración por rol
- ✅ Simplifica el código cliente

**Ejemplo de uso:**
```java
@Autowired
private UsuarioFactory usuarioFactory;

// Crear un administrador
Usuario admin = usuarioFactory.crearAdministrador(
    "Admin Principal",
    "admin@agrosoft.local",
    "admin123",
    "+57-3000000000",
    "1234567890",
    adminRole
);
```

---

## 📝 Notas

- Todos los patrones GoF implementados deben estar en esta carpeta
- Cada patrón debe tener documentación clara de su propósito y uso
- Los patrones deben seguir las mejores prácticas de diseño

---

**Última actualización:** 2025-01-XX

