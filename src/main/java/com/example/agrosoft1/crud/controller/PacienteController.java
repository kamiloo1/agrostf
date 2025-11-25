package com.example.agrosoft1.crud.controller;

import com.example.agrosoft1.crud.entity.Paciente;
import com.example.agrosoft1.crud.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    @Autowired
    private PacienteRepository pacienteRepository;

    // Listar pacientes
    /**
     * @return
     */
    @GetMapping
    public List<Paciente> listar() {
        return pacienteRepository.findAll();
    }

    // Guardar paciente
    @PostMapping
    public Paciente guardar(@RequestBody Paciente paciente) {
        return pacienteRepository.save(paciente);
    }

    // Eliminar paciente
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        pacienteRepository.deleteById(id);
    }

    // Editar paciente
    @PutMapping("/{id}")
    public Paciente actualizar(@PathVariable Long id, @RequestBody Paciente paciente) {
        paciente.setId(id);
        return pacienteRepository.save(paciente);
    }
}
