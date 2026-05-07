package com.fuerzapp.dto;

import com.fuerzapp.enums.Rol;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UsuarioAdminRequest {

    @NotBlank
    private String nombre;

    @NotBlank
    private String apellidos;

    @Email
    @NotBlank
    private String email;

    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    private String telefono;

    @NotNull
    private Rol rol;
}
