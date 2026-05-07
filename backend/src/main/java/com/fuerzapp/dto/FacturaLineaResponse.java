package com.fuerzapp.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FacturaLineaResponse {
    private String descripcion;
    private BigDecimal precioUnitario;
    private Integer cantidad;
    private BigDecimal subtotal;
}
