package com.example.agrosoft1.crud.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para la entidad {@code Tratamiento}.
 */
public class TratamientoDTO implements Serializable {

    private Long id;
    private Long ganadoId;
    private String ganadoCodigo;
    private String tipoTratamiento;
    private LocalDate fechaTratamiento;
    private String observaciones;
    private String veterinarioResponsable;
    private BigDecimal costo;
    private LocalDateTime fechaCreacion;

    public TratamientoDTO() {
    }

    public TratamientoDTO(Long id,
                          Long ganadoId,
                          String ganadoCodigo,
                          String tipoTratamiento,
                          LocalDate fechaTratamiento,
                          String observaciones,
                          String veterinarioResponsable,
                          BigDecimal costo,
                          LocalDateTime fechaCreacion) {
        this.id = id;
        this.ganadoId = ganadoId;
        this.ganadoCodigo = ganadoCodigo;
        this.tipoTratamiento = tipoTratamiento;
        this.fechaTratamiento = fechaTratamiento;
        this.observaciones = observaciones;
        this.veterinarioResponsable = veterinarioResponsable;
        this.costo = costo;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGanadoId() {
        return ganadoId;
    }

    public void setGanadoId(Long ganadoId) {
        this.ganadoId = ganadoId;
    }

    public String getGanadoCodigo() {
        return ganadoCodigo;
    }

    public void setGanadoCodigo(String ganadoCodigo) {
        this.ganadoCodigo = ganadoCodigo;
    }

    public String getTipoTratamiento() {
        return tipoTratamiento;
    }

    public void setTipoTratamiento(String tipoTratamiento) {
        this.tipoTratamiento = tipoTratamiento;
    }

    public LocalDate getFechaTratamiento() {
        return fechaTratamiento;
    }

    public void setFechaTratamiento(LocalDate fechaTratamiento) {
        this.fechaTratamiento = fechaTratamiento;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getVeterinarioResponsable() {
        return veterinarioResponsable;
    }

    public void setVeterinarioResponsable(String veterinarioResponsable) {
        this.veterinarioResponsable = veterinarioResponsable;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}

