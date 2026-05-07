package com.fuerzapp.controller;

import com.fuerzapp.dto.EntrenamientoResponse;
import com.fuerzapp.dto.GimnasioRequest;
import com.fuerzapp.dto.GimnasioResponse;
import com.fuerzapp.dto.PerfilUsuarioRequest;
import com.fuerzapp.dto.RegistroUsuarioRequest;
import com.fuerzapp.dto.SuscripcionActivaInfoResponse;
import com.fuerzapp.dto.UsuarioResponse;
import com.fuerzapp.entity.Usuario;
import com.fuerzapp.enums.Rol;
import com.fuerzapp.repository.UsuarioRepository;
import com.fuerzapp.service.EntrenamientoService;
import com.fuerzapp.service.GimnasioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gimnasios")
@RequiredArgsConstructor
public class GimnasioController {

    private final GimnasioService gimnasioService;
    private final EntrenamientoService entrenamientoService;
    private final UsuarioRepository usuarioRepository;

    // Devuelve los gimnasios del usuario autenticado (propietario o entrenador)
    @GetMapping("/mis-gimnasios")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<GimnasioResponse>> misGimnasios(Authentication auth) {
        Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<GimnasioResponse> resultado = switch (usuario.getRol()) {
            case ENTRENADOR -> gimnasioService.listarPorEntrenador(usuario.getId());
            case CLIENTE    -> gimnasioService.listarPorCliente(usuario.getId());
            default         -> gimnasioService.listarPorPropietario(usuario.getId());
        };

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROPIETARIO', 'ADMIN_PLATAFORMA')")
    public ResponseEntity<GimnasioResponse> obtenerGimnasio(@PathVariable Long id) {
        return ResponseEntity.ok(gimnasioService.obtenerPorId(id));
    }

    @PatchMapping("/{id}/datos")
    @PreAuthorize("hasAnyRole('PROPIETARIO', 'ADMIN_PLATAFORMA')")
    public ResponseEntity<GimnasioResponse> actualizarDatos(
            @PathVariable Long id,
            @Valid @RequestBody GimnasioRequest request) {
        return ResponseEntity.ok(gimnasioService.actualizarDatosPropietario(id, request));
    }

    // ─── Entrenadores ────────────────────────────────────────────────────────

    @GetMapping("/{id}/entrenadores")
    @PreAuthorize("hasAnyRole('PROPIETARIO', 'ADMIN_PLATAFORMA')")
    public ResponseEntity<List<UsuarioResponse>> listarEntrenadores(@PathVariable Long id) {
        return ResponseEntity.ok(gimnasioService.listarEntrenadores(id));
    }

    @PostMapping("/{id}/entrenadores")
    @PreAuthorize("hasAnyRole('PROPIETARIO', 'ADMIN_PLATAFORMA')")
    public ResponseEntity<UsuarioResponse> altaEntrenador(
            @PathVariable Long id,
            @Valid @RequestBody RegistroUsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                gimnasioService.darDeAltaEntrenador(id, request)
        );
    }

    @DeleteMapping("/{id}/entrenadores/{entrenadorId}")
    @PreAuthorize("hasAnyRole('PROPIETARIO', 'ADMIN_PLATAFORMA')")
    public ResponseEntity<Void> bajaEntrenador(
            @PathVariable Long id,
            @PathVariable Long entrenadorId) {
        gimnasioService.darDeBajaEntrenador(id, entrenadorId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/entrenadores/{entrenadorId}/reactivar")
    @PreAuthorize("hasAnyRole('PROPIETARIO', 'ADMIN_PLATAFORMA')")
    public ResponseEntity<Void> reactivarEntrenador(
            @PathVariable Long id,
            @PathVariable Long entrenadorId) {
        gimnasioService.reactivarEntrenador(id, entrenadorId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/entrenadores/{entrenadorId}/entrenamientos")
    @PreAuthorize("hasAnyRole('PROPIETARIO', 'ADMIN_PLATAFORMA')")
    public ResponseEntity<List<EntrenamientoResponse>> entrenamientosDeEntrenador(
            @PathVariable Long id,
            @PathVariable Long entrenadorId) {
        return ResponseEntity.ok(entrenamientoService.listarPorGimnasioYEntrenador(id, entrenadorId));
    }

    @PatchMapping("/{id}/entrenadores/{entrenadorId}/perfil")
    @PreAuthorize("hasAnyRole('PROPIETARIO', 'ADMIN_PLATAFORMA')")
    public ResponseEntity<UsuarioResponse> actualizarPerfilEntrenador(
            @PathVariable Long id,
            @PathVariable Long entrenadorId,
            @Valid @RequestBody PerfilUsuarioRequest request) {
        return ResponseEntity.ok(gimnasioService.actualizarPerfilEntrenador(id, entrenadorId, request));
    }

    // ─── Clientes ────────────────────────────────────────────────────────────

    @GetMapping("/{id}/clientes")
    @PreAuthorize("hasAnyRole('PROPIETARIO', 'ADMIN_PLATAFORMA')")
    public ResponseEntity<List<UsuarioResponse>> listarClientes(@PathVariable Long id) {
        return ResponseEntity.ok(gimnasioService.listarClientes(id));
    }

    @PostMapping("/{id}/clientes")
    @PreAuthorize("hasAnyRole('PROPIETARIO', 'ADMIN_PLATAFORMA')")
    public ResponseEntity<UsuarioResponse> altaCliente(
            @PathVariable Long id,
            @Valid @RequestBody RegistroUsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                gimnasioService.darDeAltaCliente(id, request)
        );
    }

    @DeleteMapping("/{id}/clientes/{clienteId}")
    @PreAuthorize("hasAnyRole('PROPIETARIO', 'ADMIN_PLATAFORMA')")
    public ResponseEntity<Void> bajaCliente(
            @PathVariable Long id,
            @PathVariable Long clienteId) {
        gimnasioService.darDeBajaCliente(id, clienteId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/clientes/{clienteId}/reactivar")
    @PreAuthorize("hasAnyRole('PROPIETARIO', 'ADMIN_PLATAFORMA')")
    public ResponseEntity<Void> reactivarCliente(
            @PathVariable Long id,
            @PathVariable Long clienteId) {
        gimnasioService.reactivarCliente(id, clienteId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/clientes/{clienteId}/suscripcion-activa")
    @PreAuthorize("hasAnyRole('PROPIETARIO', 'ADMIN_PLATAFORMA')")
    public ResponseEntity<?> suscripcionActivaCliente(
            @PathVariable Long id,
            @PathVariable Long clienteId) {
        return gimnasioService.obtenerSuscripcionActivaCliente(id, clienteId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PatchMapping("/{id}/clientes/{clienteId}/perfil")
    @PreAuthorize("hasAnyRole('PROPIETARIO', 'ADMIN_PLATAFORMA')")
    public ResponseEntity<UsuarioResponse> actualizarPerfilCliente(
            @PathVariable Long id,
            @PathVariable Long clienteId,
            @Valid @RequestBody PerfilUsuarioRequest request) {
        return ResponseEntity.ok(gimnasioService.actualizarPerfilCliente(id, clienteId, request));
    }
}
