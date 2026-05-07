package com.fuerzapp.controller;

import com.fuerzapp.dto.*;
import com.fuerzapp.entity.Usuario;
import com.fuerzapp.repository.UsuarioRepository;
import com.fuerzapp.service.GimnasioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN_PLATAFORMA')")
public class AdminController {

    private final GimnasioService gimnasioService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // ─── Gimnasios ────────────────────────────────────────────────────────────

    @GetMapping("/gimnasios")
    public ResponseEntity<List<GimnasioResponse>> listarGimnasios() {
        return ResponseEntity.ok(gimnasioService.listarTodos());
    }

    @PostMapping("/gimnasios")
    public ResponseEntity<GimnasioResponse> crearGimnasio(@Valid @RequestBody AltaGimnasioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                gimnasioService.crearGimnasioConPropietario(request)
        );
    }

    @PutMapping("/gimnasios/{id}")
    public ResponseEntity<GimnasioResponse> actualizarGimnasio(
            @PathVariable Long id,
            @Valid @RequestBody GimnasioRequest request) {
        return ResponseEntity.ok(gimnasioService.actualizarGimnasio(id, request));
    }

    @PatchMapping("/gimnasios/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(
            @PathVariable Long id,
            @RequestParam boolean activo) {
        gimnasioService.cambiarEstado(id, activo);
        return ResponseEntity.noContent().build();
    }

    // ─── Usuarios ─────────────────────────────────────────────────────────────

    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {
        return ResponseEntity.ok(
                usuarioRepository.findAll().stream()
                        .map(this::toUsuarioResponse)
                        .toList()
        );
    }

    @PostMapping("/usuarios")
    public ResponseEntity<UsuarioResponse> crearUsuario(@Valid @RequestBody UsuarioAdminRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con ese email");
        }
        Usuario u = new Usuario();
        u.setNombre(request.getNombre());
        u.setApellidos(request.getApellidos());
        u.setEmail(request.getEmail());
        u.setPassword(passwordEncoder.encode(request.getPassword()));
        u.setTelefono(request.getTelefono());
        u.setRol(request.getRol());
        return ResponseEntity.status(HttpStatus.CREATED).body(toUsuarioResponse(usuarioRepository.save(u)));
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioResponse> actualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioAdminRequest request) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
        if (!u.getEmail().equals(request.getEmail()) && usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con ese email");
        }
        u.setNombre(request.getNombre());
        u.setApellidos(request.getApellidos());
        u.setEmail(request.getEmail());
        u.setTelefono(request.getTelefono());
        u.setRol(request.getRol());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            u.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        return ResponseEntity.ok(toUsuarioResponse(usuarioRepository.save(u)));
    }

    @PatchMapping("/usuarios/{id}/estado")
    public ResponseEntity<Void> cambiarEstadoUsuario(
            @PathVariable Long id,
            @RequestParam boolean activo) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
        u.setActivo(activo);
        usuarioRepository.save(u);
        return ResponseEntity.noContent().build();
    }

    // ─── Helper ───────────────────────────────────────────────────────────────

    private UsuarioResponse toUsuarioResponse(Usuario u) {
        UsuarioResponse r = new UsuarioResponse();
        r.setId(u.getId());
        r.setNombre(u.getNombre());
        r.setApellidos(u.getApellidos());
        r.setEmail(u.getEmail());
        r.setTelefono(u.getTelefono());
        r.setActivo(u.getActivo());
        r.setFechaRegistro(u.getFechaRegistro());
        r.setRol(u.getRol());
        return r;
    }
}
