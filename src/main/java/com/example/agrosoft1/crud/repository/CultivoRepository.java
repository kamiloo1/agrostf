package com.example.agrosoft1.crud.repository;

import com.example.agrosoft1.crud.entity.Cultivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CultivoRepository extends JpaRepository<Cultivo, Long> {
    
    // Métodos adicionales si los necesitas
}
