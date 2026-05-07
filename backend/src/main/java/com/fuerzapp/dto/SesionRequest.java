package com.fuerzapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SesionRequest {

    @NotNull
    private LocalDateTime fechaHora;

    private Integer duracionMinutos;
    private String ubicacion;

    // Solo para sesiones GRUPAL. Null para INDIVIDUAL
    private Integer capacidadMaxima;
}
