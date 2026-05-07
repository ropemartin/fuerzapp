package com.fuerzapp.service;

import com.fuerzapp.dto.EjercicioRequest;
import com.fuerzapp.dto.EjercicioResponse;
import com.fuerzapp.enums.GrupoMuscular;

import java.util.List;

public interface EjercicioService {

    List<EjercicioResponse> listarDisponiblesPorGimnasio(Long gimnasioId, GrupoMuscular grupoMuscular);
    EjercicioResponse obtenerPorId(Long id);
    EjercicioResponse crearEjercicioEnGimnasio(Long gimnasioId, EjercicioRequest request);
    EjercicioResponse actualizar(Long id, EjercicioRequest request);
    void eliminar(Long id);
}
