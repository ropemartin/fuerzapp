package com.fuerzapp.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExtraResponse {

    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Boolean activo;
    private Long gimnasioId;
}
