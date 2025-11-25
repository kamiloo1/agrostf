package com.example.agrosoft1.crud.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cultivos")
public class Cultivo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    // Campos adicionales para compatibilidad con el código existente
    // Estos se almacenarán como NULL en la BD hasta que se migre la tabla
    @Transient
    private String tipo;

    @Transient
    private String area;

    @Transient
    private String estado;

    @Transient
    private java.time.LocalDate fechaSiembra;

    @Transient
    private java.time.LocalDate fechaCosecha;

    @Transient
    private String observaciones;

    @Transient
    private LocalDateTime fechaActualizacion;

    public Cultivo() {
    }

    public Cultivo(Long id, String nombre, String tipo, String area) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.area = area;
    }

    // Método helper para obtener idCultivo (compatibilidad)
    public Long getIdCultivo() {
        return id;
    }

    public void setIdCultivo(Long idCultivo) {
        this.id = idCultivo;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    // Campos transitorios para compatibilidad
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
        // Guardar en descripcion si existe
        if (this.descripcion == null || this.descripcion.isEmpty()) {
            this.descripcion = "Tipo: " + tipo;
        }
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
        // Guardar en descripcion si existe
        if (this.descripcion == null || this.descripcion.isEmpty()) {
            this.descripcion = "Área: " + area;
        } else {
            this.descripcion += "\nÁrea: " + area;
        }
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public java.time.LocalDate getFechaSiembra() {
        return fechaSiembra;
    }

    public void setFechaSiembra(java.time.LocalDate fechaSiembra) {
        this.fechaSiembra = fechaSiembra;
    }

    public java.time.LocalDate getFechaCosecha() {
        return fechaCosecha;
    }

    public void setFechaCosecha(java.time.LocalDate fechaCosecha) {
        this.fechaCosecha = fechaCosecha;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
        // Guardar en descripcion
        if (this.descripcion == null || this.descripcion.isEmpty()) {
            this.descripcion = observaciones;
        } else {
            this.descripcion += "\n" + observaciones;
        }
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
