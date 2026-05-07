package com.fuerzapp.controller;

import com.fuerzapp.dto.EjercicioRequest;
import com.fuerzapp.dto.EjercicioResponse;
import com.fuerzapp.enums.GrupoMuscular;
import com.fuerzapp.service.EjercicioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EjercicioController {

    private final EjercicioService ejercicioService;

    @GetMapping("/api/gimnasios/{gimnasioId}/ejercicios")
    @PreAuthorize("hasAnyRole('ENTRENADOR', 'PROPIETARIO', 'ADMIN_PLATAFORMA')")
    public ResponseEntity<List<EjercicioResponse>> listarPorGimnasio(
            @PathVariable Long gimnasioId,
            @RequestParam(required = false) GrupoMuscular grupoMuscular) {
        return ResponseEntity.ok(ejercicioService.listarDisponiblesPorGimnasio(gimnasioId, grupoMuscular));
    }

    @GetMapping("/api/ejercicios/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EjercicioResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ejercicioService.obtenerPorId(id));
    }

    @PostMapping("/api/gimnasios/{gimnasioId}/ejercicios")
    @PreAuthorize("hasAnyRole('ENTRENADOR', 'PROPIETARIO', 'ADMIN_PLATAFORMA')")
    public ResponseEntity<EjercicioResponse> crear(
            @PathVariable Long gimnasioId,
            @Valid @RequestBody EjercicioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ejercicioService.crearEjercicioEnGimnasio(gimnasioId, request)
        );
    }

    @PutMapping("/api/ejercicios/{id}")
    @PreAuthorize("hasAnyRole('ENTRENADOR', 'PROPIETARIO', 'ADMIN_PLATAFORMA')")
    public ResponseEntity<EjercicioResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody EjercicioRequest request) {
        return ResponseEntity.ok(ejercicioService.actualizar(id, request));
    }

    @DeleteMapping("/api/ejercicios/{id}")
    @PreAuthorize("hasAnyRole('ENTRENADOR', 'PROPIETARIO', 'ADMIN_PLATAFORMA')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ejercicioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
