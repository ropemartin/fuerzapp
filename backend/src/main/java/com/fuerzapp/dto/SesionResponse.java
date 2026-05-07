package com.fuerzapp.dto;

import com.fuerzapp.enums.TipoEntrenamiento;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SesionResponse {

    private Long id;
    private Long entrenamientoId;
    private String entrenamientoNombre;
    private TipoEntrenamiento tipoEntrenamiento;
    private String entrenadorNombre;
    private LocalDateTime fechaHora;
    private Integer duracionMinutos;
    private String ubicacion;
    private Integer capacidadMaxima;
    private Long plazasOcupadas;
    private List<EntrenamientoEjercicioResponse> ejercicios;
    private List<UsuarioResponse> clientesInscritos;
}
