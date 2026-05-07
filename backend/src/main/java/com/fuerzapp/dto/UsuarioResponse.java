package com.fuerzapp.dto;

import com.fuerzapp.enums.Rol;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UsuarioResponse {

    private Long id;
    private String nombre;
    private String apellidos;
    private String email;
    private String telefono;
    private Boolean activo;
    private LocalDateTime fechaRegistro;
    private Rol rol;
    private Boolean activoEnGimnasio;
}
