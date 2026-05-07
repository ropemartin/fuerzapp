package com.fuerzapp.controller;

import com.fuerzapp.dto.*;
import com.fuerzapp.enums.TipoEntrenamiento;
import com.fuerzapp.service.EntrenamientoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EntrenamientoController {

    private final EntrenamientoService entrenamientoService;

    // ─── Entrenamientos ───────────────────────────────────────────────────────

    // Filtro opcional: ?tipo=GRUPAL | INDIVIDUAL | ESPECIFICO
    @GetMapping("/api/gimnasios/{gimnasioId}/entrenamientos")
    @PreAuthorize("hasAnyRole('ENTRENADOR', 'PROPIETARIO', 'ADMIN_PLATAFORMA')")
    public ResponseEntity<List<EntrenamientoResponse>> listar(
            @PathVariable Long gimnasioId,
            @RequestParam(required = false) TipoEntrenamiento tipo) {
        return ResponseEntity.ok(entrenamientoService.listarPorGimnasio(gimnasioId, tipo));
    }

    @GetMapping("/api/entrenamientos/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EntrenamientoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(entrenamientoService.obtenerPorId(id));
    }

    @PostMapping("/api/gimnasios/{gimnasioId}/entrenamientos")
    @PreAuthorize("hasAnyRole('ENTRENADOR', 'PROPIETARIO')")
    public ResponseEntity<EntrenamientoResponse> crear(
            @PathVariable Long gimnasioId,
            @Valid @RequestBody EntrenamientoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                entrenamientoService.crear(gimnasioId, request)
        );
    }

    @PutMapping("/api/entrenamientos/{id}")
    @PreAuthorize("hasAnyRole('ENTRENADOR', 'PROPIETARIO')")
    public ResponseEntity<EntrenamientoResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody EntrenamientoRequest request) {
        return ResponseEntity.ok(entrenamientoService.actualizar(id, request));
    }

    @DeleteMapping("/api/entrenamientos/{id}")
    @PreAuthorize("hasAnyRole('ENTRENADOR', 'PROPIETARIO')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        entrenamientoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/api/entrenamientos/{id}/entrenador")
    @PreAuthorize("hasAnyRole('PROPIETARIO', 'ADMIN_PLATAFORMA')")
    public ResponseEntity<Void> reasignarEntrenador(
            @PathVariable Long id,
            @RequestParam Long nuevoEntrenadorId) {
        entrenamientoService.reasignarEntrenador(id, nuevoEntrenadorId);
        return ResponseEntity.noContent().build();
    }

    // ─── Ejercicios del entrenamiento ─────────────────────────────────────────

    @GetMapping("/api/entrenamientos/{id}/ejercicios")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<EntrenamientoEjercicioResponse>> listarEjercicios(@PathVariable Long id) {
        return ResponseEntity.ok(entrenamientoService.listarEjercicios(id));
    }

    @PostMapping("/api/entrenamientos/{id}/ejercicios")
    @PreAuthorize("hasAnyRole('ENTRENADOR', 'PROPIETARIO')")
    public ResponseEntity<EntrenamientoEjercicioResponse> agregarEjercicio(
            @PathVariable Long id,
            @Valid @RequestBody EntrenamientoEjercicioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                entrenamientoService.agregarEjercicio(id, request)
        );
    }

    @PutMapping("/api/entrenamientos/{entrenamientoId}/ejercicios/{eeId}")
    @PreAuthorize("hasAnyRole('ENTRENADOR', 'PROPIETARIO')")
    public ResponseEntity<EntrenamientoEjercicioResponse> actualizarEjercicio(
            @PathVariable Long entrenamientoId,
            @PathVariable Long eeId,
            @Valid @RequestBody EntrenamientoEjercicioRequest request) {
        return ResponseEntity.ok(entrenamientoService.actualizarEjercicio(entrenamientoId, eeId, request));
    }

    @DeleteMapping("/api/entrenamientos/{entrenamientoId}/ejercicios/{eeId}")
    @PreAuthorize("hasAnyRole('ENTRENADOR', 'PROPIETARIO')")
    public ResponseEntity<Void> eliminarEjercicio(
            @PathVariable Long entrenamientoId,
            @PathVariable Long eeId) {
        entrenamientoService.eliminarEjercicio(eeId);
        return ResponseEntity.noContent().build();
    }

    // ─── Sesiones (GRUPAL e INDIVIDUAL) ──────────────────────────────────────

    @GetMapping("/api/entrenamientos/{id}/sesiones")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SesionResponse>> listarSesiones(@PathVariable Long id) {
        return ResponseEntity.ok(entrenamientoService.listarSesiones(id));
    }

    // Próximas sesiones de un gimnasio completo
    @GetMapping("/api/gimnasios/{gimnasioId}/sesiones")
    @PreAuthorize("hasAnyRole('ENTRENADOR', 'PROPIETARIO', 'ADMIN_PLATAFORMA', 'CLIENTE')")
    public ResponseEntity<List<SesionResponse>> sesionesGimnasio(@PathVariable Long gimnasioId) {
        return ResponseEntity.ok(entrenamientoService.listarSesionesFuturasPorGimnasio(gimnasioId));
    }

    @PostMapping("/api/entrenamientos/{id}/sesiones")
    @PreAuthorize("hasAnyRole('ENTRENADOR', 'PROPIETARIO')")
    public ResponseEntity<SesionResponse> crearSesion(
            @PathVariable Long id,
            @Valid @RequestBody SesionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                entrenamientoService.crearSesion(id, request)
        );
    }

    @PutMapping("/api/sesiones/{sesionId}")
    @PreAuthorize("hasAnyRole('ENTRENADOR', 'PROPIETARIO')")
    public ResponseEntity<SesionResponse> actualizarSesion(
            @PathVariable Long sesionId,
            @Valid @RequestBody SesionRequest request) {
        return ResponseEntity.ok(entrenamientoService.actualizarSesion(sesionId, request));
    }

    @DeleteMapping("/api/sesiones/{sesionId}")
    @PreAuthorize("hasAnyRole('ENTRENADOR', 'PROPIETARIO')")
    public ResponseEntity<Void> eliminarSesion(@PathVariable Long sesionId) {
        entrenamientoService.eliminarSesion(sesionId);
        return ResponseEntity.noContent().build();
    }

    // ─── Inscripción del cliente en sesión GRUPAL ─────────────────────────────

    @PostMapping("/api/sesiones/{sesionId}/inscripcion")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Void> inscribirse(@PathVariable Long sesionId) {
        entrenamientoService.inscribirseEnSesion(sesionId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/api/sesiones/{sesionId}/inscripcion")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Void> cancelarInscripcion(@PathVariable Long sesionId) {
        entrenamientoService.cancelarInscripcion(sesionId);
        return ResponseEntity.noContent().build();
    }

    // ─── Asignar cliente a sesión INDIVIDUAL (entrenador) ────────────────────

    @PostMapping("/api/sesiones/{sesionId}/asignar-cliente")
    @PreAuthorize("hasAnyRole('ENTRENADOR', 'PROPIETARIO')")
    public ResponseEntity<SesionResponse> asignarCliente(
            @PathVariable Long sesionId,
            @Valid @RequestBody AsignarClienteRequest request) {
        return ResponseEntity.ok(entrenamientoService.asignarClienteASesion(sesionId, request));
    }

    // ─── Entrenamientos ESPECÍFICOS ───────────────────────────────────────────

    @PostMapping("/api/entrenamientos/{entrenamientoId}/asignar-cliente")
    @PreAuthorize("hasAnyRole('ENTRENADOR', 'PROPIETARIO')")
    public ResponseEntity<EntrenamientoEspecificoResponse> asignarEspecifico(
            @PathVariable Long entrenamientoId,
            @Valid @RequestBody AsignarClienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                entrenamientoService.asignarEspecifico(entrenamientoId, request)
        );
    }

    @PatchMapping("/api/entrenamientos-especificos/{id}/completado")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ENTRENADOR', 'PROPIETARIO')")
    public ResponseEntity<EntrenamientoEspecificoResponse> marcarCompletado(
            @PathVariable Long id,
            @RequestParam boolean completado) {
        return ResponseEntity.ok(entrenamientoService.marcarCompletado(id, completado));
    }

    // ─── Vista del cliente ────────────────────────────────────────────────────

    @GetMapping("/api/clientes/{clienteId}/sesiones")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ENTRENADOR', 'PROPIETARIO')")
    public ResponseEntity<List<SesionResponse>> sesionesCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(entrenamientoService.listarSesionesPorCliente(clienteId));
    }

    @GetMapping("/api/clientes/{clienteId}/entrenamientos-especificos")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ENTRENADOR', 'PROPIETARIO')")
    public ResponseEntity<List<EntrenamientoEspecificoResponse>> especificosCliente(
            @PathVariable Long clienteId) {
        return ResponseEntity.ok(entrenamientoService.listarEspecificosPorCliente(clienteId));
    }
}
