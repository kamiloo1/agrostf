package com.example.agrosoft1.crud.repository;

import com.example.agrosoft1.crud.entity.PlantillaCorreo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlantillaCorreoRepository extends JpaRepository<PlantillaCorreo, Integer> {

    /**
     * Busca todas las plantillas activas
     */
    List<PlantillaCorreo> findByActivoTrue();

    /**
     * Busca plantillas por categoría
     */
    List<PlantillaCorreo> findByCategoriaAndActivoTrue(String categoria);

    /**
     * Busca plantilla por nombre
     */
    Optional<PlantillaCorreo> findByNombre(String nombre);
}

