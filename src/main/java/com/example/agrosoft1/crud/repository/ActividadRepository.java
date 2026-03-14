package com.example.agrosoft1.crud.repository;

import com.example.agrosoft1.crud.entity.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ActividadRepository extends JpaRepository<Actividad, Long> {
    
    // Buscar actividades por cultivo (usando el id de la entidad Cultivo)
    List<Actividad> findByCultivo_Id(Long idCultivo);
    
    // Buscar actividades por tipo
    List<Actividad> findByTipoActividad(String tipoActividad);
    
    // Buscar actividades por estado
    List<Actividad> findByEstado(String estado);
    
    // Buscar actividades por trabajador
    List<Actividad> findByTrabajadorResponsable(String trabajadorResponsable);
    
    // Buscar actividades por fecha
    List<Actividad> findByFechaActividad(LocalDate fecha);
    
    // Contar actividades por estado
    long countByEstado(String estado);
    
    // Contar actividades por cultivo
    long countByCultivo_Id(Long idCultivo);
}
