package com.example.agrosoft1.crud.repository;

import com.example.agrosoft1.crud.entity.Notificacion;
import com.example.agrosoft1.crud.entity.Usuario;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByUsuarioOrderByFechaCreacionDesc(Usuario usuario, Pageable pageable);

    long countByUsuarioAndLeidaFalse(Usuario usuario);

    long countByUsuario(Usuario usuario);

    @Modifying
    @Query("UPDATE Notificacion n SET n.leida = true WHERE n.usuario = :usuario")
    void marcarTodasComoLeidas(@Param("usuario") Usuario usuario);

    @Modifying
    @Query("UPDATE Notificacion n SET n.leida = true WHERE n.id = :id AND n.usuario = :usuario")
    int marcarComoLeida(@Param("id") Long id, @Param("usuario") Usuario usuario);
}
