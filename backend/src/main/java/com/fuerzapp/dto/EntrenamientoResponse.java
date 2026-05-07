package com.fuerzapp.dto;

import com.fuerzapp.enums.TipoEntrenamiento;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EntrenamientoResponse {

    private Long id;
    private String nombre;
    private String descripcion;
    private TipoEntrenamiento tipo;
    private LocalDateTime fechaCreacion;
    private Boolean activo;
    private Long gimnasioId;
    private Long entrenadorId;
    private String entrenadorNombre;
    private List<EntrenamientoEjercicioResponse> ejercicios;
}
