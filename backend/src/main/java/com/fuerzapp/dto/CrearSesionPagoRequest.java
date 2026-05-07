package com.fuerzapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CrearSesionPagoRequest {

    @NotNull
    private Long clienteSuscripcionId;

    // URL a la que redirige Stripe tras un pago exitoso
    @NotNull
    private String successUrl;

    // URL a la que redirige Stripe si el usuario cancela
    @NotNull
    private String cancelUrl;
}
