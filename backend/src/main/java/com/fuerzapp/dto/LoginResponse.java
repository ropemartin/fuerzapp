package com.fuerzapp.dto;

import com.fuerzapp.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {

    private Long id;
    private String token;
    private String email;
    private String nombre;
    private Rol rol;
}
