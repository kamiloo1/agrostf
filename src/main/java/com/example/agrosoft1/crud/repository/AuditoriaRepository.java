package com.example.agrosoft1.crud.repository;

import com.example.agrosoft1.crud.entity.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {
    java.util.List<Auditoria> findTop100ByOrderByFechaDesc();
}
