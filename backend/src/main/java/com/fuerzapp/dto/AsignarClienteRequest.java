package com.fuerzapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AsignarClienteRequest {

    @NotNull
    private Long clienteId;

    private String notas;
}
