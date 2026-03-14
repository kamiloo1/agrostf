package com.example.agrosoft1.crud.repository;

import com.example.agrosoft1.crud.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByNombre(String nombre);

    /** Roles que puede elegir un usuario al registrarse (excluye ADMIN; solo el admin asigna ese rol). */
    List<Role> findByNombreNotIgnoreCase(String nombre);
}

