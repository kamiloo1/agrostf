# PATRÓN DAO - Data Access Object (Estructural)

## Ubicación
**Directorio**: `repository/`

## Propósito
Separar la lógica de acceso a datos de la lógica de negocio. El patrón DAO proporciona una abstracción entre la aplicación y la base de datos, permitiendo cambiar la implementación de persistencia sin afectar el código de negocio.

## ¿Por qué se implementó?
- **Separación de responsabilidades**: Separa la lógica de acceso a datos de la lógica de negocio
- **Abstracción**: Oculta los detalles de implementación de la base de datos
- **Mantenibilidad**: Facilita cambiar la implementación de persistencia sin modificar el código de negocio
- **Testabilidad**: Permite crear mocks fácilmente para pruebas unitarias
- **Reutilización**: Los métodos de acceso a datos se pueden reutilizar en diferentes servicios
- **Estándar Spring**: Utiliza Spring Data JPA que implementa el patrón DAO de forma estándar

## Repositorios Implementados

### 1. UsuarioRepository
**Ubicación**: `repository/UsuarioRepository.java`

**Métodos principales**:
- `findByCorreo(String correo)`: Busca usuario por correo electrónico
- `findByNumeroDocumento(String numeroDocumento)`: Busca usuario por número de documento
- `findByCorreoAndPassword(String correo, String password)`: Busca usuario por correo y contraseña
- Hereda métodos de `JpaRepository`: `save()`, `findAll()`, `findById()`, `deleteById()`, etc.

### 2. CultivoRepository
**Ubicación**: `repository/CultivoRepository.java`

**Métodos principales**:
- Hereda métodos de `JpaRepository` para operaciones CRUD completas

### 3. GanadoRepository
**Ubicación**: `repository/GanadoRepository.java`

**Métodos principales**:
- `findByActivoTrue()`: Busca ganado activo
- Hereda métodos de `JpaRepository` para operaciones CRUD completas

### 4. TratamientoRepository
**Ubicación**: `repository/TratamientoRepository.java`

**Métodos principales**:
- Hereda métodos de `JpaRepository` para operaciones CRUD completas

### 5. ActividadRepository
**Ubicación**: `repository/ActividadRepository.java`

**Métodos principales**:
- Hereda métodos de `JpaRepository` para operaciones CRUD completas

### 6. RoleRepository
**Ubicación**: `repository/RoleRepository.java`

**Métodos principales**:
- `findByNombre(String nombre)`: Busca rol por nombre
- Hereda métodos de `JpaRepository` para operaciones CRUD completas

### 7. PacienteRepository
**Ubicación**: `repository/PacienteRepository.java`

**Métodos principales**:
- Hereda métodos de `JpaRepository` para operaciones CRUD completas

## Estructura del Patrón

```
Service Layer (Lógica de Negocio)
    ↓ usa
Repository Layer (DAO - Acceso a Datos)
    ↓ usa
Entity Layer (Modelo de Datos)
    ↓ mapea
Database (MySQL)
```

## Ejemplo de Uso

```java
@Service
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository; // DAO
    
    public Usuario guardarUsuario(Usuario usuario) {
        // Lógica de negocio (validaciones, encriptación, etc.)
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        
        // Acceso a datos a través del DAO
        return usuarioRepository.save(usuario);
    }
}
```

## Ventajas del Patrón DAO

1. **Desacoplamiento**: El código de negocio no depende de la implementación de la base de datos
2. **Flexibilidad**: Se puede cambiar de MySQL a PostgreSQL sin modificar servicios
3. **Testabilidad**: Fácil crear mocks de repositorios para pruebas
4. **Mantenibilidad**: Cambios en la estructura de datos se centralizan en los repositorios
5. **Estándar**: Spring Data JPA proporciona implementación automática del patrón

## Relación con Spring Data JPA

Spring Data JPA implementa automáticamente el patrón DAO:
- Las interfaces que extienden `JpaRepository` son los DAOs
- Spring genera automáticamente la implementación
- Proporciona métodos CRUD estándar sin necesidad de código adicional
- Permite definir métodos personalizados usando convenciones de nombres

