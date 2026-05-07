package com.fuerzapp.entity;

import com.fuerzapp.enums.Dificultad;
import com.fuerzapp.enums.GrupoMuscular;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ejercicio")
@Data
@NoArgsConstructor
public class Ejercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "grupo_muscular", nullable = false)
    private GrupoMuscular grupoMuscular;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Dificultad dificultad;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "imagen_url")
    private String imagenUrl;

    @Column(name = "es_predefinido", nullable = false)
    private Boolean esPredefinido = false;

    // NULL si el ejercicio es de la biblioteca global (predefinido)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gimnasio_id")
    private Gimnasio gimnasio;

    // NULL si el ejercicio es predefinido
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por_id")
    private Usuario creadoPor;
}
