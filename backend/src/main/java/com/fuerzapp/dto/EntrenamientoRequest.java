package com.fuerzapp.dto;

import com.fuerzapp.enums.TipoEntrenamiento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EntrenamientoRequest {

    @NotBlank
    private String nombre;

    private String descripcion;

    @NotNull
    private TipoEntrenamiento tipo;
}
