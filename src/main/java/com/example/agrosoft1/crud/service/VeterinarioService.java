package com.example.agrosoft1.crud.service;

import com.example.agrosoft1.crud.repository.TratamientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VeterinarioService {

    @Autowired
    private TratamientoRepository tratamientoRepository;

    public int contarTratamientos() {
        return (int) tratamientoRepository.count();
    }

    public int contarReportes() {
        // Por ahora retornamos el mismo conteo, se puede ajustar según la lógica de negocio
        return (int) tratamientoRepository.count();
    }

    public int contarRevisiones() {
        // Por ahora retornamos el mismo conteo, se puede ajustar según la lógica de negocio
        return (int) tratamientoRepository.count();
    }
}
