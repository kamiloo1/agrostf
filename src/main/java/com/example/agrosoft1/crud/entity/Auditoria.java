package com.example.agrosoft1.crud.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Registro de auditoría para acciones críticas (crear, actualizar, eliminar) en entidades principales.
 */
@Entity
@Table(name = "auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario", length = 150)
    private String usuario;

    @Column(nullable = false, length = 50)
    private String accion;

    @Column(nullable = false, length = 80)
    private String entidad;

    @Column(name = "id_entidad", length = 50)
    private String idEntidad;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(length = 500)
    private String detalles;

    public Auditoria() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getEntidad() {
        return entidad;
    }

    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

    public String getIdEntidad() {
        return idEntidad;
    }

    public void setIdEntidad(String idEntidad) {
        this.idEntidad = idEntidad;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }
}
