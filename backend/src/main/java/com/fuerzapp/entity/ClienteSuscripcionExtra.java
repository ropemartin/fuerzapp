package com.fuerzapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cliente_suscripcion_extra")
@Data
@NoArgsConstructor
public class ClienteSuscripcionExtra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_suscripcion_id", nullable = false)
    private ClienteSuscripcion clienteSuscripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "extra_id", nullable = false)
    private Extra extra;

    @Column(name = "precio_contratacion", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioContratacion;

    @Column(name = "fecha_contratacion", nullable = false)
    private LocalDateTime fechaContratacion = LocalDateTime.now();
}
