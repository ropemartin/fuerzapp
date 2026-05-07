package com.fuerzapp.controller;

import com.fuerzapp.dto.*;
import com.fuerzapp.service.SuscripcionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SuscripcionController {

    private final SuscripcionService suscripcionService;

    // ─── Tipos de suscripción ─────────────────────────────────────────────────

    @GetMapping("/api/gimnasios/{gimnasioId}/suscripciones")
    @PreAuthorize("hasAnyRole('PROPIETARIO', 'ADMIN_PLATAFORMA', 'ENTRENADOR', 'CLIENTE')")
    public ResponseEntity<List<TipoSuscripcionResponse>> listarTipos(@PathVariable Long gimnasioId) {
        return ResponseEntity.ok(suscripcionService.listarTipos(gimnasioId));
    }

    @PostMapping("/api/gimnasios/{gimnasioId}/suscripciones")
    @PreAuthorize("hasAnyRole('PROPIETARIO', 'ADMIN_PLATAFORMA')")
    public ResponseEntity<TipoSuscripcionResponse> crearTipo(
            @PathVariable Long gimnasioId,
            @Valid @RequestBody TipoSuscripcionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                suscripcionService.crearTipo(gimnasioId, request)
        );
    }

    @PutMapping("/api/gimnasios/{gimnasioId}/suscripciones/{tipoId}")
    @PreAuthorize("hasAnyRole('PROPIETARIO', 'ADMIN_PLATAFORMA')")
    public ResponseEntity<TipoSuscripcionResponse> actualizarTipo(
            @PathVariable Long gimnasioId,
            @PathVariable Long tipoId,
            @Valid @RequestBody TipoSuscripcionRequest request) {
        return ResponseEntity.ok(suscripcionService.actualizarTipo(tipoId, request));
    }

    @PatchMapping("/api/gimnasios/{gimnasioId}/suscripciones/{tipoId}/estado")
    @PreAuthorize("hasAnyRole('PROPIETARIO', 'ADMIN_PLATAFORMA')")
    public ResponseEntity<Void> cambiarEstadoTipo(
            @PathVariable Long gimnasioId,
            @PathVariable Long tipoId,
            @RequestParam boolean activo) {
        suscripcionService.cambiarEstadoTipo(tipoId, activo);
        return ResponseEntity.noContent().build();
    }

    // ─── Extras ──────────────────────────────────────────────────────────────

    @GetMapping("/api/gimnasios/{gimnasioId}/extras")
    @PreAuthorize("hasAnyRole('PROPIETARIO', 'ADMIN_PLATAFORMA', 'ENTRENADOR', 'CLIENTE')")
    public ResponseEntity<List<ExtraResponse>> listarExtras(@PathVariable Long gimnasioId) {
        return ResponseEntity.ok(suscripcionService.listarExtras(gimnasioId));
    }

    @PostMapping("/api/gimnasios/{gimnasioId}/extras")
    @PreAuthorize("hasAnyRole('PROPIETARIO', 'ADMIN_PLATAFORMA')")
    public ResponseEntity<ExtraResponse> crearExtra(
            @PathVariable Long gimnasioId,
            @Valid @RequestBody ExtraRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                suscripcionService.crearExtra(gimnasioId, request)
        );
    }

    @PutMapping("/api/gimnasios/{gimnasioId}/extras/{extraId}")
    @PreAuthorize("hasAnyRole('PROPIETARIO', 'ADMIN_PLATAFORMA')")
    public ResponseEntity<ExtraResponse> actualizarExtra(
            @PathVariable Long gimnasioId,
            @PathVariable Long extraId,
            @Valid @RequestBody ExtraRequest request) {
        return ResponseEntity.ok(suscripcionService.actualizarExtra(extraId, request));
    }

    @PatchMapping("/api/gimnasios/{gimnasioId}/extras/{extraId}/estado")
    @PreAuthorize("hasAnyRole('PROPIETARIO', 'ADMIN_PLATAFORMA')")
    public ResponseEntity<Void> cambiarEstadoExtra(
            @PathVariable Long gimnasioId,
            @PathVariable Long extraId,
            @RequestParam boolean activo) {
        suscripcionService.cambiarEstadoExtra(extraId, activo);
        return ResponseEntity.noContent().build();
    }

    // ─── Suscripciones de clientes ───────────────────────────────────────────

    @GetMapping("/api/gimnasios/{gimnasioId}/suscriptores")
    @PreAuthorize("hasAnyRole('PROPIETARIO', 'ADMIN_PLATAFORMA')")
    public ResponseEntity<List<ClienteSuscripcionResponse>> listarSuscriptores(@PathVariable Long gimnasioId) {
        return ResponseEntity.ok(suscripcionService.listarSuscripcionesGimnasio(gimnasioId));
    }

    @GetMapping("/api/clientes/{clienteId}/suscripcion")
    @PreAuthorize("hasAnyRole('PROPIETARIO', 'ADMIN_PLATAFORMA', 'CLIENTE')")
    public ResponseEntity<ClienteSuscripcionResponse> obtenerSuscripcionCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(suscripcionService.obtenerSuscripcionCliente(clienteId));
    }

    @PostMapping("/api/clientes/{clienteId}/suscripcion")
    @PreAuthorize("hasAnyRole('PROPIETARIO', 'ADMIN_PLATAFORMA')")
    public ResponseEntity<ClienteSuscripcionResponse> asignarSuscripcion(
            @PathVariable Long clienteId,
            @Valid @RequestBody ClienteSuscripcionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                suscripcionService.asignarSuscripcion(clienteId, request)
        );
    }

    @PutMapping("/api/clientes/{clienteId}/suscripcion")
    @PreAuthorize("hasAnyRole('PROPIETARIO', 'ADMIN_PLATAFORMA')")
    public ResponseEntity<ClienteSuscripcionResponse> cambiarSuscripcion(
            @PathVariable Long clienteId,
            @Valid @RequestBody ClienteSuscripcionRequest request) {
        return ResponseEntity.ok(suscripcionService.cambiarTipoSuscripcion(clienteId, request));
    }

    @PostMapping("/api/clientes/{clienteId}/suscripcion/extras/{extraId}")
    @PreAuthorize("hasAnyRole('PROPIETARIO', 'ADMIN_PLATAFORMA')")
    public ResponseEntity<ClienteSuscripcionResponse> agregarExtra(
            @PathVariable Long clienteId,
            @PathVariable Long extraId) {
        return ResponseEntity.ok(suscripcionService.agregarExtra(clienteId, extraId));
    }
}
