package com.example.agrosoft1.crud.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "ganado")
@Getter
@Setter
public class Ganado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ganado")
    private Long idGanado;

    @Column(nullable = false)
    private String tipo;

    @Column
    private String raza;

    @Column
    private Integer edad;

    @Column(precision = 10, scale = 2)
    private Double peso;

    @Column(name = "estado_salud")
    private String estadoSalud;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "fecha_creacion")
    private java.time.LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private Boolean activo = true;
}
