package com.example.agrosoft1.crud.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "paciente")
@Getter
@Setter
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column
    private String especie;

    @Column
    private String raza;

    @Column
    private String edad;

    @Column
    private String estado;

    @Column
    private String observaciones;
}
