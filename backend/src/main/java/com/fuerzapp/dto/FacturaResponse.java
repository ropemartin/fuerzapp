package com.fuerzapp.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FacturaResponse {

    private Long id;
    private Long pagoId;
    private String numeroFactura;
    private LocalDateTime fechaEmision;
    private BigDecimal importeTotal;
    private String clienteNombre;
    private String clienteEmail;
    private String tipoSuscripcionNombre;
    private String gimnasioNombre;
}
