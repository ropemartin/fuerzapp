package com.fuerzapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sesion_cliente",
        uniqueConstraints = @UniqueConstraint(columnNames = {"sesion_id", "cliente_id"}))
@Data
@NoArgsConstructor
public class SesionCliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sesion_id", nullable = false)
    private Sesion sesion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    @Column(name = "fecha_inscripcion", nullable = false)
    private LocalDateTime fechaInscripcion = LocalDateTime.now();

    private Boolean asistio = false;

    @Column(columnDefinition = "TEXT")
    private String notas;
}
