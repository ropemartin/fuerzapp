package com.fuerzapp.service;

import com.fuerzapp.dto.CrearSesionPagoRequest;
import com.fuerzapp.dto.FacturaResponse;
import com.fuerzapp.dto.PagoResponse;
import com.fuerzapp.dto.SesionPagoResponse;

import java.util.List;

public interface PagoService {

    SesionPagoResponse crearSesionPago(CrearSesionPagoRequest request);
    PagoResponse registrarPagoEfectivo(Long clienteSuscripcionId);
    void procesarWebhook(String payload, String sigHeader);
    List<PagoResponse> listarPagosPorCliente(Long clienteId);
    List<PagoResponse> listarPagosPorGimnasio(Long gimnasioId);
    FacturaResponse obtenerFacturaPorPago(Long pagoId);
}
