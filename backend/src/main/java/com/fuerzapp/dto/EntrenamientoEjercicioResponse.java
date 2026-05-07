package com.fuerzapp.dto;

import lombok.Data;

@Data
public class EntrenamientoEjercicioResponse {

    private Long id;
    private Integer orden;
    private Long ejercicioId;
    private String ejercicioNombre;
    private String ejercicioDescripcion;
    private String grupoMuscular;
    private String dificultad;
    private String imagenUrl;
    private String videoUrl;
    private Integer series;
    private Integer repeticiones;
    private Integer duracionSegundos;
    private Integer descansoSegundos;
    private String notas;
}
