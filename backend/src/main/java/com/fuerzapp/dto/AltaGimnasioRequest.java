package com.fuerzapp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AltaGimnasioRequest {

    @Valid
    @NotNull
    private GimnasioRequest gimnasio;

    @Valid
    @NotNull
    private RegistroUsuarioRequest propietario;
}
