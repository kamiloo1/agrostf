package com.example.agrosoft1.crud.dto;

import java.io.Serializable;

/**
 * DTO para la entidad {@code Paciente}.
 */
public class PacienteDTO implements Serializable {

    private Long id;
    private String nombre;
    private String especie;
    private String raza;
    private String edad;
    private String estado;
    private String observaciones;

    public PacienteDTO() {
    }

    public PacienteDTO(Long id,
                       String nombre,
                       String especie,
                       String raza,
                       String edad,
                       String estado,
                       String observaciones) {
        this.id = id;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.edad = edad;
        this.estado = estado;
        this.observaciones = observaciones;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}

