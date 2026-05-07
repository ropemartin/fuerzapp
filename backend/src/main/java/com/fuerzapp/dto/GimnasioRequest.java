package com.fuerzapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GimnasioRequest {

    @NotBlank
    private String nombre;

    private String direccion;
    private String ciudad;
    private String telefono;

    @Email
    private String email;

    private String logoUrl;

    private Integer porcentajeIva;

    private Long propietarioId;
}
