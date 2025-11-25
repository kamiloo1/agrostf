package com.example.agrosoft1.crud.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para la entidad {@code Ganado}.
 */
public class GanadoDTO implements Serializable {

    private Long id;
    private String tipo;
    private String raza;
    private Integer edad;
    private Double peso;
    private String estadoSalud;
    private LocalDate fechaNacimiento;
    private LocalDateTime fechaCreacion;
    private Integer responsableId;
    private String responsableNombre;
    private Boolean activo;

    public GanadoDTO() {
    }

    public GanadoDTO(Long id,
                     String tipo,
                     String raza,
                     Integer edad,
                     Double peso,
                     String estadoSalud,
                     LocalDate fechaNacimiento,
                     LocalDateTime fechaCreacion,
                     Integer responsableId,
                     String responsableNombre,
                     Boolean activo) {
        this.id = id;
        this.tipo = tipo;
        this.raza = raza;
        this.edad = edad;
        this.peso = peso;
        this.estadoSalud = estadoSalud;
        this.fechaNacimiento = fechaNacimiento;
        this.fechaCreacion = fechaCreacion;
        this.responsableId = responsableId;
        this.responsableNombre = responsableNombre;
        this.activo = activo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public String getEstadoSalud() {
        return estadoSalud;
    }

    public void setEstadoSalud(String estadoSalud) {
        this.estadoSalud = estadoSalud;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Integer getResponsableId() {
        return responsableId;
    }

    public void setResponsableId(Integer responsableId) {
        this.responsableId = responsableId;
    }

    public String getResponsableNombre() {
        return responsableNombre;
    }

    public void setResponsableNombre(String responsableNombre) {
        this.responsableNombre = responsableNombre;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}

