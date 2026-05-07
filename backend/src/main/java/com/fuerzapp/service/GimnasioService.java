package com.fuerzapp.service;

import com.fuerzapp.dto.AltaGimnasioRequest;
import com.fuerzapp.dto.GimnasioRequest;
import com.fuerzapp.dto.GimnasioResponse;
import com.fuerzapp.dto.PerfilUsuarioRequest;
import com.fuerzapp.dto.RegistroUsuarioRequest;
import com.fuerzapp.dto.SuscripcionActivaInfoResponse;
import com.fuerzapp.dto.UsuarioResponse;

import java.util.List;
import java.util.Optional;

public interface GimnasioService {

    GimnasioResponse crearGimnasioConPropietario(AltaGimnasioRequest request);
    List<GimnasioResponse> listarTodos();
    GimnasioResponse obtenerPorId(Long id);
    GimnasioResponse actualizarGimnasio(Long id, GimnasioRequest request);
    GimnasioResponse actualizarDatosPropietario(Long id, GimnasioRequest request);
    void cambiarEstado(Long id, boolean activo);

    List<GimnasioResponse> listarPorPropietario(Long propietarioId);
    List<GimnasioResponse> listarPorEntrenador(Long entrenadorId);
    List<GimnasioResponse> listarPorCliente(Long clienteId);

    UsuarioResponse darDeAltaEntrenador(Long gimnasioId, RegistroUsuarioRequest request);
    List<UsuarioResponse> listarEntrenadores(Long gimnasioId);
    void darDeBajaEntrenador(Long gimnasioId, Long entrenadorId);
    void reactivarEntrenador(Long gimnasioId, Long entrenadorId);

    UsuarioResponse darDeAltaCliente(Long gimnasioId, RegistroUsuarioRequest request);
    List<UsuarioResponse> listarClientes(Long gimnasioId);
    void darDeBajaCliente(Long gimnasioId, Long clienteId);
    void reactivarCliente(Long gimnasioId, Long clienteId);
    Optional<SuscripcionActivaInfoResponse> obtenerSuscripcionActivaCliente(Long gimnasioId, Long clienteId);

    UsuarioResponse actualizarPerfilEntrenador(Long gimnasioId, Long entrenadorId, PerfilUsuarioRequest request);
    UsuarioResponse actualizarPerfilCliente(Long gimnasioId, Long clienteId, PerfilUsuarioRequest request);
}
