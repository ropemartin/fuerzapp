package com.fuerzapp.dto;

import com.fuerzapp.enums.EstadoPago;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PagoResponse {

    private Long id;
    private Long clienteSuscripcionId;
    private String clienteNombre;
    private String clienteEmail;
    private String tipoSuscripcionNombre;
    private BigDecimal importe;
    private LocalDateTime fecha;
    private EstadoPago estado;
    private Long facturaId;
    private String numeroFactura;
}
