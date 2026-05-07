package com.fuerzapp.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GimnasioResponse {

    private Long id;
    private String nombre;
    private String direccion;
    private String ciudad;
    private String telefono;
    private String email;
    private String logoUrl;
    private Integer porcentajeIva;
    private LocalDateTime fechaAlta;
    private Boolean activo;
    private Long propietarioId;
    private String propietarioNombre;
    private String propietarioEmail;
}
