package com.fuerzapp.controller;

import com.fuerzapp.dto.*;
import com.fuerzapp.service.PagoService;
import com.fuerzapp.service.PdfFacturaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;
    private final PdfFacturaService pdfFacturaService;

    // El cliente inicia el pago de su suscripción
    @PostMapping("/crear-sesion")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<SesionPagoResponse> crearSesion(
            @Valid @RequestBody CrearSesionPagoRequest request) {
        return ResponseEntity.ok(pagoService.crearSesionPago(request));
    }

    // Stripe llama a este endpoint al completarse o fallar un pago
    // Es público: la seguridad la garantiza la firma del webhook
    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        pagoService.procesarWebhook(payload, sigHeader);
        return ResponseEntity.ok().build();
    }

    // Historial de pagos del cliente
    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'PROPIETARIO', 'ADMIN_PLATAFORMA')")
    public ResponseEntity<List<PagoResponse>> pagosPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(pagoService.listarPagosPorCliente(clienteId));
    }

    // Pagos del gimnasio (vista del propietario)
    @GetMapping("/gimnasio/{gimnasioId}")
    @PreAuthorize("hasAnyRole('PROPIETARIO', 'ADMIN_PLATAFORMA')")
    public ResponseEntity<List<PagoResponse>> pagosPorGimnasio(@PathVariable Long gimnasioId) {
        return ResponseEntity.ok(pagoService.listarPagosPorGimnasio(gimnasioId));
    }

    // Factura de un pago concreto (JSON)
    @GetMapping("/{pagoId}/factura")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FacturaResponse> factura(@PathVariable Long pagoId) {
        return ResponseEntity.ok(pagoService.obtenerFacturaPorPago(pagoId));
    }

    // Descarga de factura en PDF
    @GetMapping("/{pagoId}/factura/pdf")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> facturaPdf(@PathVariable Long pagoId) {
        FacturaResponse factura = pagoService.obtenerFacturaPorPago(pagoId);
        byte[] pdf = pdfFacturaService.generar(factura);
        String nombre = "factura-" + factura.getNumeroFactura() + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombre + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
