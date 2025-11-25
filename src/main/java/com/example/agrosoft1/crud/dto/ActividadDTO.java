package com.example.agrosoft1.crud.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para la entidad {@code Actividad}.
 */
public class ActividadDTO implements Serializable {

    private Long id;
    private Long cultivoId;
    private String cultivoNombre;
    private String tipoActividad;
    private String descripcion;
    private LocalDate fechaActividad;
    private String trabajadorResponsable;
    private String estado;
    private LocalDateTime fechaCreacion;

    public ActividadDTO() {
    }

    public ActividadDTO(Long id,
                        Long cultivoId,
                        String cultivoNombre,
                        String tipoActividad,
                        String descripcion,
                        LocalDate fechaActividad,
                        String trabajadorResponsable,
                        String estado,
                        LocalDateTime fechaCreacion) {
        this.id = id;
        this.cultivoId = cultivoId;
        this.cultivoNombre = cultivoNombre;
        this.tipoActividad = tipoActividad;
        this.descripcion = descripcion;
        this.fechaActividad = fechaActividad;
        this.trabajadorResponsable = trabajadorResponsable;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCultivoId() {
        return cultivoId;
    }

    public void setCultivoId(Long cultivoId) {
        this.cultivoId = cultivoId;
    }

    public String getCultivoNombre() {
        return cultivoNombre;
    }

    public void setCultivoNombre(String cultivoNombre) {
        this.cultivoNombre = cultivoNombre;
    }

    public String getTipoActividad() {
        return tipoActividad;
    }

    public void setTipoActividad(String tipoActividad) {
        this.tipoActividad = tipoActividad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaActividad() {
        return fechaActividad;
    }

    public void setFechaActividad(LocalDate fechaActividad) {
        this.fechaActividad = fechaActividad;
    }

    public String getTrabajadorResponsable() {
        return trabajadorResponsable;
    }

    public void setTrabajadorResponsable(String trabajadorResponsable) {
        this.trabajadorResponsable = trabajadorResponsable;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}

