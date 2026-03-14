package com.example.agrosoft1.crud.repository;

import com.example.agrosoft1.crud.entity.TokenRecuperacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRecuperacionRepository extends JpaRepository<TokenRecuperacion, Integer> {

    Optional<TokenRecuperacion> findByToken(String token);

    void deleteByUsuario_Id(Integer usuarioId);
}
