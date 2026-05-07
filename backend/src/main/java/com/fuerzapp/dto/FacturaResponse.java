package com.fuerzapp.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class FacturaResponse {

    private Long id;
    private Long pagoId;
    private String numeroFactura;
    private LocalDateTime fechaEmision;

    private String clienteNombre;
    private String clienteEmail;
    private String tipoSuscripcionNombre;

    private String gimnasioNombre;
    private String gimnasioDireccion;
    private String gimnasioTelefono;
    private String gimnasioEmail;
    private String gimnasioLogoUrl;

    private BigDecimal baseImponible;
    private Integer porcentajeIva;
    private BigDecimal importeIva;
    private BigDecimal importeTotal;

    private List<FacturaLineaResponse> lineas;
}
