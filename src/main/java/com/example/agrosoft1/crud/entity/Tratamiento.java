package com.example.agrosoft1.crud.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.math.BigDecimal;

@Entity
@Table(name = "tratamientos")
@Getter
@Setter
public class Tratamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tratamiento")
    private Long idTratamiento;

    @ManyToOne
    @JoinColumn(name = "id_ganado", referencedColumnName = "id_ganado")
    private Ganado ganado;

    @Column(name = "tipo_tratamiento", nullable = false)
    private String tipoTratamiento;

    @Column(name = "fecha_tratamiento", nullable = false)
    private LocalDate fechaTratamiento;

    @Column
    private String observaciones;

    @Column(name = "veterinario_responsable")
    private String veterinarioResponsable;

    @Column(precision = 10, scale = 2)
    private BigDecimal costo;

    @Column(name = "fecha_creacion")
    private java.time.LocalDateTime fechaCreacion;
}
