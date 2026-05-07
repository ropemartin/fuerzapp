package com.fuerzapp.entity;

import com.fuerzapp.enums.EstadoPago;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pago")
@Data
@NoArgsConstructor
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_suscripcion_id", nullable = false)
    private ClienteSuscripcion clienteSuscripcion;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal importe;

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    @Column(name = "stripe_payment_intent_id")
    private String stripePaymentIntentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPago estado = EstadoPago.PENDIENTE;
}
