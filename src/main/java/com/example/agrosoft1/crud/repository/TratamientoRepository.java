package com.example.agrosoft1.crud.repository;

import com.example.agrosoft1.crud.entity.Tratamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TratamientoRepository extends JpaRepository<Tratamiento, Long> {

    /** Lista todos los tratamientos con ganado cargado (evita LazyInitializationException en la vista). */
    @Query("SELECT DISTINCT t FROM Tratamiento t LEFT JOIN FETCH t.ganado ORDER BY t.fechaTratamiento DESC")
    List<Tratamiento> findAllWithGanado();

    // Buscar tratamientos por ganado
    List<Tratamiento> findByGanadoIdGanado(Long idGanado);
    
    // Buscar tratamientos por tipo
    List<Tratamiento> findByTipoTratamiento(String tipoTratamiento);
    
    // Buscar tratamientos por veterinario
    List<Tratamiento> findByVeterinarioResponsable(String veterinarioResponsable);
    
    // Buscar tratamientos por fecha
    List<Tratamiento> findByFechaTratamiento(LocalDate fecha);
    
    // Contar tratamientos por ganado
    long countByGanadoIdGanado(Long idGanado);
}
