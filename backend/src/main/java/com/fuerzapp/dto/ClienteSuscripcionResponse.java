package com.fuerzapp.dto;

import com.fuerzapp.enums.EstadoSuscripcion;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class ClienteSuscripcionResponse {

    private Long id;
    private Long clienteId;
    private String clienteNombre;
    private String clienteEmail;
    private Long tipoSuscripcionId;
    private String tipoSuscripcionNombre;
    private BigDecimal precio;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private EstadoSuscripcion estado;
    private List<ExtraResponse> extras;
}
