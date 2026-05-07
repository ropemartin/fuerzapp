package com.fuerzapp.service;

import com.fuerzapp.dto.AsignarClienteRequest;
import com.fuerzapp.dto.EntrenamientoEjercicioRequest;
import com.fuerzapp.dto.EntrenamientoEjercicioResponse;
import com.fuerzapp.dto.EntrenamientoEspecificoResponse;
import com.fuerzapp.dto.EntrenamientoRequest;
import com.fuerzapp.dto.EntrenamientoResponse;
import com.fuerzapp.dto.SesionRequest;
import com.fuerzapp.dto.SesionResponse;
import com.fuerzapp.enums.TipoEntrenamiento;

import java.util.List;

public interface EntrenamientoService {

    List<EntrenamientoResponse> listarPorGimnasio(Long gimnasioId, TipoEntrenamiento tipo);
    List<EntrenamientoResponse> listarPorGimnasioYEntrenador(Long gimnasioId, Long entrenadorId);
    void reasignarEntrenador(Long entrenamientoId, Long nuevoEntrenadorId);
    EntrenamientoResponse obtenerPorId(Long id);
    EntrenamientoResponse crear(Long gimnasioId, EntrenamientoRequest request);
    EntrenamientoResponse actualizar(Long id, EntrenamientoRequest request);
    void eliminar(Long id);

    List<EntrenamientoEjercicioResponse> listarEjercicios(Long entrenamientoId);
    EntrenamientoEjercicioResponse agregarEjercicio(Long entrenamientoId, EntrenamientoEjercicioRequest request);
    EntrenamientoEjercicioResponse actualizarEjercicio(Long entrenamientoId, Long eeId, EntrenamientoEjercicioRequest request);
    void eliminarEjercicio(Long eeId);

    List<SesionResponse> listarSesiones(Long entrenamientoId);
    List<SesionResponse> listarSesionesFuturasPorGimnasio(Long gimnasioId);
    SesionResponse crearSesion(Long entrenamientoId, SesionRequest request);
    SesionResponse actualizarSesion(Long sesionId, SesionRequest request);
    void eliminarSesion(Long sesionId);

    void inscribirseEnSesion(Long sesionId);
    void cancelarInscripcion(Long sesionId);
    SesionResponse asignarClienteASesion(Long sesionId, AsignarClienteRequest request);

    EntrenamientoEspecificoResponse asignarEspecifico(Long entrenamientoId, AsignarClienteRequest request);
    EntrenamientoEspecificoResponse marcarCompletado(Long especificoId, boolean completado);

    List<SesionResponse> listarSesionesPorCliente(Long clienteId);
    List<EntrenamientoEspecificoResponse> listarEspecificosPorCliente(Long clienteId);
}
