package com.fuerzapp.entity;

import com.fuerzapp.enums.EstadoSuscripcion;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "cliente_suscripcion")
@Data
@NoArgsConstructor
public class ClienteSuscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_suscripcion_id", nullable = false)
    private TipoSuscripcion tipoSuscripcion;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSuscripcion estado = EstadoSuscripcion.PENDIENTE;

    @Column(name = "stripe_subscription_id")
    private String stripeSubscriptionId;
}
