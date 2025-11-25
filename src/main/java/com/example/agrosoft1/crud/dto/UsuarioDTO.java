package com.example.agrosoft1.crud.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO para la entidad {@code Usuario}.
 */
public class UsuarioDTO implements Serializable {

    private Integer id;
    private String nombre;
    private String correo;
    private String telefono;
    private String numeroDocumento;
    private String rol;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime ultimoLogin;

    public UsuarioDTO() {
    }

    public UsuarioDTO(Integer id,
                      String nombre,
                      String correo,
                      String telefono,
                      String numeroDocumento,
                      String rol,
                      Boolean activo,
                      LocalDateTime fechaCreacion,
                      LocalDateTime ultimoLogin) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.numeroDocumento = numeroDocumento;
        this.rol = rol;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
        this.ultimoLogin = ultimoLogin;
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

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getUltimoLogin() {
        return ultimoLogin;
    }

    public void setUltimoLogin(LocalDateTime ultimoLogin) {
        this.ultimoLogin = ultimoLogin;
    }
}

