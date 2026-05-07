package com.fuerzapp.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class SuscripcionActivaInfoResponse {
    private String tipoNombre;
    private LocalDate fechaFin;
}
