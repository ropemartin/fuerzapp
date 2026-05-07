package com.fuerzapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PerfilUsuarioRequest {
    @NotBlank private String nombre;
    @NotBlank private String apellidos;
    @Email @NotBlank private String email;
    private String telefono;
}
