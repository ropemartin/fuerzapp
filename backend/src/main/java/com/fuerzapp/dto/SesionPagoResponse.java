package com.fuerzapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SesionPagoResponse {

    // URL de Stripe a la que redirigir al cliente para que pague
    private String checkoutUrl;
    private String sessionId;
}
