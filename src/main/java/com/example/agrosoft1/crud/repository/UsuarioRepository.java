package com.example.agrosoft1.crud.repository;

import com.example.agrosoft1.crud.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Buscar usuario por correo
    Optional<Usuario> findByCorreo(String correo);

    // Buscar usuario por número de documento
    Optional<Usuario> findByNumeroDocumento(String numeroDocumento);

    // Buscar usuario por correo y contraseña (para compatibilidad)
    Usuario findByCorreoAndPassword(String correo, String password);

    // Métodos de compatibilidad con código existente
    default Optional<Usuario> findByEmail(String email) {
        return findByCorreo(email);
    }
}
