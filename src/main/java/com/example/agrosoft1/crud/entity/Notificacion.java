package com.example.agrosoft1.crud.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Notificación para el usuario. Se muestra en la campana del header.
 */
@Entity
@Table(name = "notificaciones")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 500)
    private String mensaje;

    @Column(nullable = false, length = 50)
    private String tipo; // GANADO, CULTIVO, TRATAMIENTO, ACTIVIDAD, SISTEMA, etc.

    @Column(name = "leida", nullable = false)
    private Boolean leida = false;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(length = 255)
    private String enlace; // URL opcional para ir al detalle

    public Notificacion() {
    }

    public Notificacion(Usuario usuario, String mensaje, String tipo) {
        this.usuario = usuario;
        this.mensaje = mensaje;
        this.tipo = tipo;
    }

    public Notificacion(Usuario usuario, String mensaje, String tipo, String enlace) {
        this.usuario = usuario;
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.enlace = enlace;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Boolean getLeida() {
        return leida;
    }

    public void setLeida(Boolean leida) {
        this.leida = leida;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getEnlace() {
        return enlace;
    }

    public void setEnlace(String enlace) {
        this.enlace = enlace;
    }
}
