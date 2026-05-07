package com.fuerzapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ClienteSuscripcionRequest {

    @NotNull
    private Long tipoSuscripcionId;

    @NotNull
    private LocalDate fechaInicio;
}
