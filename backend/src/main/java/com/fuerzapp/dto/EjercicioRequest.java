package com.fuerzapp.dto;

import com.fuerzapp.enums.Dificultad;
import com.fuerzapp.enums.GrupoMuscular;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EjercicioRequest {

    @NotBlank
    private String nombre;

    private String descripcion;

    @NotNull
    private GrupoMuscular grupoMuscular;

    @NotNull
    private Dificultad dificultad;

    private String videoUrl;
    private String imagenUrl;
}
