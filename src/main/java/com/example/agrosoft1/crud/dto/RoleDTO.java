package com.example.agrosoft1.crud.dto;

import java.io.Serializable;

/**
 * DTO para la entidad {@code Role}.
 */
public class RoleDTO implements Serializable {

    private Integer id;
    private String nombre;
    private String descripcion;

    public RoleDTO() {
    }

    public RoleDTO(Integer id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}

