package com.fuerzapp.service;

import com.fuerzapp.dto.ClienteSuscripcionRequest;
import com.fuerzapp.dto.ClienteSuscripcionResponse;
import com.fuerzapp.dto.ExtraRequest;
import com.fuerzapp.dto.ExtraResponse;
import com.fuerzapp.dto.TipoSuscripcionRequest;
import com.fuerzapp.dto.TipoSuscripcionResponse;

import java.util.List;

public interface SuscripcionService {

    List<TipoSuscripcionResponse> listarTipos(Long gimnasioId);
    TipoSuscripcionResponse crearTipo(Long gimnasioId, TipoSuscripcionRequest request);
    TipoSuscripcionResponse actualizarTipo(Long tipoId, TipoSuscripcionRequest request);
    void cambiarEstadoTipo(Long tipoId, boolean activo);

    List<ExtraResponse> listarExtras(Long gimnasioId);
    ExtraResponse crearExtra(Long gimnasioId, ExtraRequest request);
    ExtraResponse actualizarExtra(Long extraId, ExtraRequest request);
    void cambiarEstadoExtra(Long extraId, boolean activo);

    List<ClienteSuscripcionResponse> listarSuscripcionesGimnasio(Long gimnasioId);
    ClienteSuscripcionResponse obtenerSuscripcionCliente(Long clienteId);
    ClienteSuscripcionResponse asignarSuscripcion(Long clienteId, ClienteSuscripcionRequest request);
    ClienteSuscripcionResponse cambiarTipoSuscripcion(Long clienteId, ClienteSuscripcionRequest request);
    ClienteSuscripcionResponse agregarExtra(Long clienteId, Long extraId);
}
