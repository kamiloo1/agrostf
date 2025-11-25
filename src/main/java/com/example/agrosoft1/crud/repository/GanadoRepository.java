package com.example.agrosoft1.crud.repository;

import com.example.agrosoft1.crud.entity.Ganado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GanadoRepository extends JpaRepository<Ganado, Long> {
    
    List<Ganado> findByTipo(String tipo);
    
    List<Ganado> findByEstadoSalud(String estadoSalud);
    
    // Buscar ganado por raza
    List<Ganado> findByRaza(String raza);
    
    // Contar ganado por tipo
    long countByTipo(String tipo);
    
    long countByEstadoSalud(String estadoSalud);

    List<Ganado> findByActivoTrue();

    long countByActivoTrue();

    long countByEstadoSaludAndActivoTrue(String estadoSalud);
}
