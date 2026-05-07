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

    // Biblioteca global predefinida (accesible para todos los autenticados)
    @GetMapping("/api/ejercicios")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<EjercicioResponse>> listarPredefinidos() {
        return ResponseEntity.ok(ejercicioService.listarPredefinidos());
    }

    // Todos los ejercicios disponibles para un gimnasio (globales + propios)
    // Filtro opcional por grupo muscular: ?grupoMuscular=PECHO
    @GetMapping("/api/gimnasios/{gimnasioId}/ejercicios")
    @PreAuthorize("hasAnyRole('ENTRENADOR', 'PROPIETARIO', 'ADMIN_PLATAFORMA')")
    public ResponseEntity<List<EjercicioResponse>> listarPorGimnasio(
            @PathVariable Long gimnasioId,
            @RequestParam(required = false) GrupoMuscular grupoMuscular) {
        return ResponseEntity.ok(ejercicioService.listarDisponiblesPorGimnasio(gimnasioId, grupoMuscular));
    }

    // Detalle de un ejercicio
    @GetMapping("/api/ejercicios/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EjercicioResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ejercicioService.obtenerPorId(id));
    }

    // El entrenador crea un ejercicio en su gimnasio
    @PostMapping("/api/gimnasios/{gimnasioId}/ejercicios")
    @PreAuthorize("hasAnyRole('ENTRENADOR', 'PROPIETARIO', 'ADMIN_PLATAFORMA')")
    public ResponseEntity<EjercicioResponse> crear(
            @PathVariable Long gimnasioId,
            @Valid @RequestBody EjercicioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ejercicioService.crearEjercicioEnGimnasio(gimnasioId, request)
        );
    }

    // Editar un ejercicio (solo los propios del gimnasio, no los predefinidos)
    @PutMapping("/api/ejercicios/{id}")
    @PreAuthorize("hasAnyRole('ENTRENADOR', 'PROPIETARIO', 'ADMIN_PLATAFORMA')")
    public ResponseEntity<EjercicioResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody EjercicioRequest request) {
        return ResponseEntity.ok(ejercicioService.actualizar(id, request));
    }

    // Eliminar un ejercicio (no se pueden eliminar los predefinidos)
    @DeleteMapping("/api/ejercicios/{id}")
    @PreAuthorize("hasAnyRole('ENTRENADOR', 'PROPIETARIO', 'ADMIN_PLATAFORMA')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ejercicioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
