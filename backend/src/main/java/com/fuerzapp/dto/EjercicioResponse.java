package com.fuerzapp.dto;

import com.fuerzapp.enums.Dificultad;
import com.fuerzapp.enums.GrupoMuscular;
import lombok.Data;

@Data
public class EjercicioResponse {

    private Long id;
    private String nombre;
    private String descripcion;
    private GrupoMuscular grupoMuscular;
    private Dificultad dificultad;
    private String videoUrl;
    private String imagenUrl;
    private Long gimnasioId;
    private String creadoPorNombre;
}
