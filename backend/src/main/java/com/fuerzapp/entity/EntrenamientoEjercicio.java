package com.fuerzapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "entrenamiento_ejercicio")
@Data
@NoArgsConstructor
public class EntrenamientoEjercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entrenamiento_id", nullable = false)
    private Entrenamiento entrenamiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ejercicio_id", nullable = false)
    private Ejercicio ejercicio;

    @Column(nullable = false)
    private Integer orden;

    private Integer series;
    private Integer repeticiones;

    @Column(name = "duracion_segundos")
    private Integer duracionSegundos;

    @Column(name = "descanso_segundos")
    private Integer descansoSegundos;

    @Column(columnDefinition = "TEXT")
    private String notas;
}
