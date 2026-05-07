package com.fuerzapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "entrenador_gimnasio",
        uniqueConstraints = @UniqueConstraint(columnNames = {"entrenador_id", "gimnasio_id"}))
@Data
@NoArgsConstructor
public class EntrenadorGimnasio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entrenador_id", nullable = false)
    private Usuario entrenador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gimnasio_id", nullable = false)
    private Gimnasio gimnasio;

    @Column(name = "fecha_alta", nullable = false)
    private LocalDateTime fechaAlta = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean activo = true;
}
