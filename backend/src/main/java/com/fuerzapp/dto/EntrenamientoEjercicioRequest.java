package com.fuerzapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EntrenamientoEjercicioRequest {

    @NotNull
    private Long ejercicioId;

    @NotNull
    @Min(1)
    private Integer orden;

    private Integer series;
    private Integer repeticiones;
    private Integer duracionSegundos;
    private Integer descansoSegundos;
    private String notas;
}
