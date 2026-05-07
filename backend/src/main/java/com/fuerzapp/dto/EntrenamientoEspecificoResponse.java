package com.fuerzapp.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EntrenamientoEspecificoResponse {

    private Long id;
    private Long entrenamientoId;
    private String entrenamientoNombre;
    private String entrenadorNombre;
    private Long clienteId;
    private String clienteNombre;
    private LocalDateTime fechaAsignacion;
    private String notas;
    private Boolean completado;
    private List<EntrenamientoEjercicioResponse> ejercicios;
}
