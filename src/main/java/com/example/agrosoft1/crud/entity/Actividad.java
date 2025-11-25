package com.example.agrosoft1.crud.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "actividades")
@Getter
@Setter
public class Actividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_actividad")
    private Long idActividad;

    @ManyToOne
    @JoinColumn(name = "id_cultivo", referencedColumnName = "id")
    private Cultivo cultivo;

    @Column(name = "tipo_actividad", nullable = false)
    private String tipoActividad;

    @Column
    private String descripcion;

    @Column(name = "fecha_actividad", nullable = false)
    private LocalDate fechaActividad;

    @Column(name = "trabajador_responsable")
    private String trabajadorResponsable;

    @Column
    private String estado;

    @Column(name = "fecha_creacion")
    private java.time.LocalDateTime fechaCreacion;
}
